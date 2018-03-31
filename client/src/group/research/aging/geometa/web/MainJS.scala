package group.research.aging.geometa.web

import mhtml._
import mhtml.mount
import org.scalajs.dom
import shared.Hello

import scala.scalajs.js.annotation._
import scala.xml.Elem

@JSExportTopLevel("MainJS")
object MainJS {

  @JSExport
  def page(page: String, parameters: String*) = {
    dom.console.log(s"GEOmetadb Web applications loaded:\n page = ${page} \nwith parameters = ${parameters.reduce(_ + " " + _)}")
    page match {
      case "gsm" =>
      case _ => dom.console.error("unkwong page")
    }
  }

  // A single State => Html function for the entire page:
  //def view(state: Rx[State]): xml.Node =

  val state: Rx[State] = Var(TestState)

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




  val loadPage = Var(LoadPage)
  /*
  // Probably implemented with Var, but we can look at them as Rx. Note that the
  // type can easily me made more precise by using <: Action instead:
  val action1_clicks: Rx[Action] = ...
  val action2_inputs: Rx[Action] = ...
  val action3_AJAX:   Rx[Action] = ...
  val action4_timer:  Rx[TimeAction] = ...


  // Let's merges all actions together:
  val allActions: Rx[Action] =
    action1_clicks merge
      action2_inputs merge
      action3_AJAX   merge
      action4_timer
  */

  // Compute the new state given an action and a previous state:
  // (I'm really not convinced by the name)
  def reducer(previousState: State, action: Action): State = action match {
    case _ =>
      dom.console.error(s"no state change for ${action} with ${previousState}")
      previousState
  }

  // The application State, probably initialize that from local store / DB
  // updates could also be save on every update.
  //val store: Rx[State] = allActions.foldp(State.empty)(reducer)

  // Tie everything together:
  //mount(DefaultState, view(store))

  val div = dom.document.getElementById("main")
  mount(div, component)

}
