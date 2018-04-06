package group.research.aging.geometa.web

import group.research.aging.geometa.web.actions.LoadedSequencing
import mhtml.Rx

import scala.xml.Elem

class SequencingView(loadedSequencing: Rx[LoadedSequencing]) extends Base {

  /*
  val component: Elem = <table id="workflows" class="ui small blue striped celled table">
    <thead>
      { headers.map(h=> header(h)) }
    </thead>
    <tbody>
      { data.map(d=> d.map(dataRow)) }
    </tbody>
  </table>
  */

  protected def header(row: List[String]) = <tr> {row.map(c=> <th>{c}</th>)} </tr>

  protected def dataRow(row: List[String]) = <tr> {row.map(c=> <td>{c}</td>)} </tr>

}
