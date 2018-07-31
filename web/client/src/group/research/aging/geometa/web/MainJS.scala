package group.research.aging.geometa.web

import group.research.aging.geometa.web.samples.{SamplesQueryView, SamplesView}
import group.research.aging.utils.SimpleSourceFormatter
import mhtml._
import org.scalajs.dom
import wvlet.log.LogFormatter.SourceCodeLogFormatter
import wvlet.log.{LogLevel, LogSupport, Logger}

import scala.scalajs.js.annotation._
import scala.util._
import scalajs.concurrent.JSExecutionContext.Implicits.queue

@JSExportTopLevel("MainJS")
object MainJS extends Base{

  // Set the default log formatter
  Logger.setDefaultFormatter(SimpleSourceFormatter)
  Logger.setDefaultLogLevel(LogLevel.DEBUG)

  debug("MainJS STARTED!!!!!!")

  val toLoad: Var[actions.ToLoad] = Var(actions.NothingToLoad)
  val loaded: Var[actions.Loaded] = Var(actions.NothingLoaded)
  val throwError: Var[actions.ExplainedError] = Var(actions.ExplainedError.empty)
  val updateUI: Var[actions.UpdateUI] = Var(actions.NotUpdateUI)

  //val allActions: Rx[actions.Action] = toLoad.merge(loaded).merge(updateUI).merge(throwError).dropRepeats //because Merge is super-buggy in monadic-html and produces a lot of redundant events
  //ugly workaround for https://github.com/OlivierBlanvillain/monadic-html/issues/98
  val allActions: Var[actions.Action] = Var(actions.NothingLoaded)
  protected def uglyUpdate(rxes: Rx[actions.Action]*) = {
    for(r <- rxes) r.impure.run(v=> allActions := v)
  }
  uglyUpdate(toLoad, throwError, updateUI, loaded)
  val state: Var[states.State] = Var(states.State.empty)

  //workaround to avoid foldp issues
  allActions.impure.run{ a=>
    val currentState: states.State = state.now
    val newState = reducer(currentState, a)
    if(newState != currentState) state := newState
  }
  //val state: Rx[states.State] = allActions.foldp(states.State.empty){ case (s, a) => reducer(s, a)}.dropRepeats

  @JSExport
  def page(page: String, parameters: String*) = {
	  //dom.window.alert(page)
    val pString = parameters.foldLeft(""){ case (acc, el) => acc + " " + el}
    info(s"GEOmetadb Web applications loaded:\n page = ${page} \nwith parameters = ${pString}")
    toLoad := actions.LoadPageData(page)
  }

  val samplesView = new SamplesView(state.map(s=>s.sequencing).dropRepeats, toLoad, updateUI)
  val errorView = new ErrorsView(state.map(s=>s.errors))
  //{  tableView.component }

  val component =
    <div id="sequencing">
      {  errorView.component  }
      {  samplesView.component }
    </div>

  import cats.effect.IO
  import hammock.{Hammock, _}
  import hammock.circe.implicits._
  import hammock.js.Interpreter
  import hammock.marshalling._
  //import io.circe.generic.auto._

  implicit val interpreter: Interpreter[IO] = Interpreter[IO]

  lazy val loadReducer: Reducer = {

    case (previous, actions.NothingToLoad |
                    actions.ExplainedError.empty |
                    actions.NothingLoaded |
                    actions.NotUpdateUI) => previous

    case (previous, actions.LoadPageData(page, parameters)) =>
      val pms = if(parameters.isEmpty) "" else "/" + parameters.mkString("-")
      val path = s"/data/${page + pms}"
      val u = Uri(path = path)
      Hammock.request(Method.GET, u, Map.empty)
        .as[actions.LoadedSequencing]
        .exec[IO].unsafeToFuture()
        .onComplete{
          case Success(results) =>
            loaded := results
          case Failure(th) =>
            error(th)
            throwError := actions.ExplainedError(s"loading page ${page} with uri ${u} failed", th.getMessage)
        }
      previous.copy(page = page, parameters = parameters)

    case (previous, seq: actions.LoadedSequencing) =>
      //val data_new: List[List[String]] = samples.map(s=>s.asStringList)
      //val headers_new: List[String] = if(samples.isEmpty) Nil else samples.head.fieldNames
      //previous.copy( "gsm", headers_new, data_new, queryInfo = queryInfo)
      previous.copy(sequencing = seq)
  }


  lazy val UIReducer: Reducer = {
    case (previous, actions.EvalJS(code)) =>
      scalajs.js.eval(code)
      previous
  }

  lazy val errorReducer: Reducer = {
    case (previous, e: actions.ExplainedError) =>
      error(e)
      previous.copy(errors = e::previous.errors)
  }

  def onOther : Reducer = {
    case (previous, action) =>
      error(s"no state change for ${action} with ${previous}")
      previous
  }

  lazy val onReduce: Reducer = loadReducer.orElse(errorReducer).orElse(UIReducer).orElse(onOther)


  // Compute the new state given an action and a previous state:
  // (I'm really not convinced by the name)
  def reducer(previousState: states.State, action: actions.Action): states.State = {
    debug(action)
    onReduce(previousState, action)
  }

  val div = dom.document.getElementById("main")
  mount(div, component)

}
