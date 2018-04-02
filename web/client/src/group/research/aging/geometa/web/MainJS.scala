package group.research.aging.geometa.web

import cats.effect.IO
import hammock.{Hammock, _}
import hammock.circe.implicits._
import hammock.js.Interpreter
import hammock.marshalling._
import io.circe.generic.auto._
import mhtml.{mount, _}
import org.scalajs.dom
import wvlet.log.LogSupport

import scala.collection.immutable._
import scala.scalajs.js.annotation._
import scala.util._
import scalajs.concurrent.JSExecutionContext.Implicits.queue

@JSExportTopLevel("MainJS")
object MainJS  extends LogSupport{

  implicit val interpreter = Interpreter[IO]
  type Reducer = PartialFunction[(states.State, actions.Action), states.State]

  @JSExport
  def page(page: String, parameters: String*) = {
	  //dom.window.alert(page)
    info(s"GEOmetadb Web applications loaded:\n page = ${page} \nwith parameters = ${parameters.reduce(_ + " " + _)}")
    //toLoad := actions.LoadPage(page)
  }

  val state: Rx[states.State] = Var(states.State.empty)

  val headers: Rx[List[String]] = state.map(s=>s.headers)

  val data: Rx[List[List[String]]] = state.map(s=> s.data)

  val component= <table id="workflows" class="ui small blue striped celled table">
    <thead>
        { headers.map(h=> header(h)) }
    </thead>
    <tbody>
      { data.map(d=> d.map(dataRow)) }
    </tbody>
  </table>

  def header(row: List[String]) = <tr> {row.map(c=> <th>{c}</th>)} </tr>

  def dataRow(row: List[String]) = <tr> {row.map(c=> <td>{c}</td>)} </tr>

  val toLoad: Var[actions.ToLoad] = Var(
    actions.NothingToLoad
  )

  val loaded: Var[actions.Loaded] = Var(actions.NothingLoaded)

  val allActions: Rx[actions.Action] = toLoad merge loaded merge loaded

  val loadReducer: Reducer = {

    case (previous, actions.NothingToLoad) =>
      previous

    case (previous, actions.LoadPage(page)) =>
      //toLoad :=
      Hammock.request(Method.GET, uri"/view/${page}", Map.empty)
        .as[actions.LoadedSequencing]
        .exec[IO].unsafeToFuture()
        .onComplete{
          case Success(results) => loaded := results
          case Failure(th) => error(th)
        }
      previous

    case (previous, actions.LoadedSequencing( samples,  limit, offset)) =>
      val data_new: List[List[String]] = samples.map(s=>s.asStringList)
      val headers_new: List[String] = if(samples.isEmpty) Nil else samples.head.fieldNames
      previous.copy( "gsm", headers_new, data_new)
  }

  def onOther : Reducer = {
    case (previous, action) =>
      error(s"no state change for ${action} with ${previous}")
      previous
  }

  // Compute the new state given an action and a previous state:
  // (I'm really not convinced by the name)
  def reducer(previousState: states.State, action: actions.Action): states.State = loadReducer.orElse(onOther).apply(previousState, action)

  // The application State, probably initialize that from local store / DB
  // updates could also be save on every update.
  //val store: Rx[states.State] = allactions.foldp(states.DefaultState)(reducer)
  val div = dom.document.getElementById("main")
  mount(div, component)

}
