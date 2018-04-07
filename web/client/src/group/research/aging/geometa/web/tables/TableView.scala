package group.research.aging.geometa.web.tables

import group.research.aging.geometa.web.Base
import mhtml.Rx

import scala.xml.Elem

class TableView(headers: Rx[List[String]], data: Rx[List[List[String]]]) extends Base {

  val component: Elem = <table id="workflows" class="ui small blue striped celled table">
    <thead>
      { headers.map(h=> header(h)) }
    </thead>
    <tbody>
      { data.map(d=> d.map(dataRow)) }
    </tbody>
  </table>

  protected def header(row: List[String]) = <tr> {row.map(c=> <th>{c}</th>)} </tr>

  protected def dataRow(row: List[String]) = <tr> {row.map(c=> <td>{c}</td>)} </tr>

}
