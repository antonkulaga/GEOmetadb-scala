package group.research.aging.geometa.web

import wvlet.log.LogSupport

import mhtml._
import mhtml.mount
import org.scalajs.dom
import group.research.aging.geometa.web.states

import scala.scalajs.js.annotation._
import scala.xml.Elem

@JSExportTopLevel("MainJS")
object MainJS  extends LogSupport{

  //Logger.setDefaultHandler(new JSConsoleLogHandler)

  @JSExport
  def page(page: String, parameters: String*) = {
    info(s"GEOmetadb Web applications loaded:\n page = ${page} \nwith parameters = ${parameters.reduce(_ + " " + _)}")
    loadPage := actions.LoadPage(page)
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

  val loadPage: Var[actions.Action] = Var(
    actions.LoadPage.test
  )

  val loaded: Var[actions.Action] = Var(actions.NothingLoaded)

  val allActions: Rx[actions.Action] = loadPage merge loaded


  // Compute the new state given an action and a previous state:
  // (I'm really not convinced by the name)
  def reducer(previousState: states.State, action: actions.Action): states.State = action match {
    case _ =>
      dom.console.error(s"no state change for ${action} with ${previousState}")
      previousState
  }

  // The application State, probably initialize that from local store / DB
  // updates could also be save on every update.
  //val store: Rx[states.State] = allactions.foldp(states.DefaultState)(reducer)

  val div = dom.document.getElementById("main")
  mount(div, component)

}
