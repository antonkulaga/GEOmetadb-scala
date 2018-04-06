package group.research.aging.geometa.web

import group.research.aging.geometa.web.actions.ExplainedError
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
  val throwError: Var[ExplainedError] = Var(ExplainedError.empty)

  val allActions: Rx[actions.Action] = toLoad merge loaded merge throwError

  @JSExport
  def page(page: String, parameters: String*) = {
	  //dom.window.alert(page)
    val pString = parameters.foldLeft(""){ case (acc, el) => acc + " " + el}
    info(s"GEOmetadb Web applications loaded:\n page = ${page} \nwith parameters = ${pString}")
    toLoad := actions.LoadPage(page)
  }

  val state: Rx[states.State] = allActions.dropRepeats.foldp(states.State.empty){ case (s, a) => reducer(s, a)}

  //val species: Rx

  val queryView = new QueryView(state.map(s=>s.queryInfo), toLoad)
  val tableView = new TableView(state.map(s=>s.headers), state.map(s=> s.data))
  val errorView = new ErrorsView(state.map(s=>s.errors))

  val component =
    <div id="sequencing">
      {  queryView.component  }
      {  errorView.component  }
      {  tableView.component }
    </div>

  import cats.effect.IO
  import hammock.{Hammock, _}
  import hammock.circe.implicits._
  import hammock.js.Interpreter
  import hammock.marshalling._
  //import io.circe.generic.auto._

  implicit val interpreter = Interpreter[IO]


  lazy val loadReducer: Reducer = {

    case (previous, actions.NothingToLoad) => previous
    case (previous, ExplainedError.empty) => previous
    case (previous, actions.NothingLoaded) => previous

    case (previous, actions.LoadPage(page)) =>
      val u = Uri(path = s"/view/${page}")
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
      previous.copy(page = page)

    case (previous, actions.LoadedSequencing( queryInfo, samples,  limit, offset)) =>
      val data_new: List[List[String]] = samples.map(s=>s.asStringList)
      val headers_new: List[String] = if(samples.isEmpty) Nil else samples.head.fieldNames
      previous.copy( "gsm", headers_new, data_new, queryInfo = queryInfo)
  }

  lazy val errorReducer: Reducer = {
    case (previos, e: actions.ExplainedError) =>
      error(e)
      previos.copy(errors = e::previos.errors)
  }

  def onOther : Reducer = {
    case (previous, action) =>
      error(s"no state change for ${action} with ${previous}")
      previous
  }

  lazy val onReduce: Reducer = loadReducer.orElse(errorReducer).orElse(onOther)


  // Compute the new state given an action and a previous state:
  // (I'm really not convinced by the name)
  def reducer(previousState: states.State, action: actions.Action): states.State = {
    debug(action)
    onReduce(previousState, action)
  }


  val div = dom.document.getElementById("main")
  mount(div, component)

}
