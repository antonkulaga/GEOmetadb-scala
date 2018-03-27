package group.research.aging.geometa.web

import mhtml._
import mhtml.mount
import org.scalajs.dom
import shared.Hello


import scala.scalajs.js.annotation._

@JSExportTopLevel("MainJS")
object MainJS {

  @JSExport
  def page(page: String, parameters: String*) = {
    dom.console.log(s"GEOmetadb Web applications loaded:\n page = ${page} \nwith parameters = ${parameters.reduce(_ + " " + _)}")
  }


  val component= <table id="workflows" class="ui small blue striped celled table">
    <thead>
      <tr>
        <th>name/id</th>
        <th>status</th>
        <th>start</th>
        <th>end</th>
        <th>workflow</th>
        <th>calls and failures</th>
      </tr>
    </thead>
    <tbody>
    </tbody>
  </table>

  val div = dom.document.getElementById("main")
  mount(div, component)

}
trait Page
case object Table extends Page {

}