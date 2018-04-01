package group.research.aging.geometa.web

import group.research.aging.geometa.web.actions.LoadPage
import wvlet.log.LogSupport
import mhtml._
import mhtml.mount
import org.scalajs.dom
import group.research.aging.geometa.web.states
import hammock.Hammock
import hammock.js.Interpreter

import scala.concurrent.Future
import scala.util._
import scala.scalajs.js.annotation._
import scala.xml.Elem
import cats.effect.IO
import group.research.aging.geometa.models.Sequencing_GSM
import io.circe.generic.auto._
import hammock._
import hammock.marshalling._
import hammock.js.Interpreter
import hammock.circe.implicits._

@JSExportTopLevel("MainJS")
object MainJS  extends LogSupport{

  implicit val interpreter = Interpreter[Future]

  type Reducer = PartialFunction[(states.State, actions.Action), states.State]

  //Logger.setDefaultHandler(new JSConsoleLogHandler)

  @JSExport
  def page(page: String, parameters: String*) = {
    info(s"GEOmetadb Web applications loaded:\n page = ${page} \nwith parameters = ${parameters.reduce(_ + " " + _)}")
    toLoad := actions.LoadPage(page)
  }

  // A single State => Html function for the entire page:
  //def view(state: Rx[State]): xml.Node =

  val state: Rx[states.State] = Var(states.TestState)

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
    actions.ToLoad.empty
  )

  val loaded: Var[actions.Action] = Var(actions.NothingLoaded)

  val allActions: Rx[actions.Action] = toLoad merge loaded merge loaded

  val loadReducer: Reducer = {
    case (previos, LoadPage(page)) =>
      //toLoad :=
      Hammock.request(Method.GET, uri"/view/${page}", Map.empty).as[actions.LoadSequencing].exec[Future].onComplete{
        case Success(results) =>
          loaded := results
        case Failure(th) => error(th)
      }
      previos

    case (previos, actions.LoadSequencing(
      sequencing: List[Sequencing_GSM],  limit: Long, offset: Long)) =>

      previos
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
