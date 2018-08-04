package group.research.aging.geometa.web.samples

import group.research.aging.geometa.models.Sequencing
import group.research.aging.geometa.web.Base
import group.research.aging.geometa.web.actions.{LoadedSequencing, ToLoad, UpdateUI}
import mhtml.{Rx, Var}

import scala.xml.Elem

class SamplesView(loadedSequencing: Rx[LoadedSequencing],
                  toLoad: Var[ToLoad],
                  toUpdate: Var[UpdateUI])
  extends Base {


  val queryView = new SamplesQueryView(loadedSequencing.map(_.suggestionInfo).dropRepeats, toLoad, toUpdate)


  val sequencing = loadedSequencing.map(_.sequencing).dropRepeats


  val component: Elem =
    <table class="ui small blue striped celled table">
      { queryView.component }
      <tbody>
        { sequencing.map{s=>s.map(row=>dataRow(row))} }
      </tbody>
    </table>


  protected def dataRow(row: Sequencing) =
    <tr>
    <td>{row.title}</td>
    <td><a href={s"https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=${row.gsm}"} target="_blank">{row.gsm}</a></td>
    <td><a href={s"https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=${row.gse}"} target="_blank">{row.gse}</a></td>
    <td>{row.organism}</td>
    <td>{row.molecule}</td>
    <td>{row.sequencer}</td>
    <td>{row.characteristics}</td>
    <td>{row.description}</td>
    <td>{row.treatment_protocol}</td>
    <td>{row.extract_protocol}</td>
    <td>{row.source_name}</td>
    <td>{row.data_processing}</td>
    <td>{row.submission_date}</td>
    <td>{row.last_update_date}</td>
    <td>{row.status}</td>
    <td>{row.`type`}</td>
    <td>{row.contact}</td>
    <td><a href={s"https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=${row.gpl}"} target="_blank">{row.gpl}</a></td>
    </tr>

}
