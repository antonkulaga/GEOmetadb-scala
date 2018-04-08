package group.research.aging.geometa.web.samples

import group.research.aging.geometa.models.Sequencing
import group.research.aging.geometa.web.Base
import group.research.aging.geometa.web.actions.{LoadedSequencing, ToLoad, UpdateUI}
import group.research.aging.geometa.web.states.SamplesQueryInfo
import mhtml.{Rx, Var}

import scala.xml.Elem

class SamplesView(loadedSequencing: Rx[LoadedSequencing], toLoad: Var[ToLoad], toUpdate: Var[UpdateUI]) extends Base {


  val queryView = new SamplesQueryView(loadedSequencing.map(_.queryInfo).dropRepeats, toLoad, toUpdate)


  val sequencing = loadedSequencing.map(_.sequencing).dropRepeats
  /**
    * ID: String,
    * title: String,
    * gsm: String,
    * series_id: String,
    * gpl: String,
    * status: String,
    * submission_date: String,
    * last_update_date: String,
    * `type`: String,
    * source_name_ch1: String,
    * organism_ch1: String,
    * characteristics_ch1: String,
    * molecule_ch1: String,
    * //label_ch1: String,
    * treatment_protocol_ch1: String,
    * extract_protocol_ch1: String,
    * //label_protocol_ch1: String,
    * //hyb_protocol: String,
    * description: String,
    * data_processing: String,
    * contact: String,
    * //supplementary_file: String,
    * data_row_count: Double,
    * channel_count: Double,
    * sequencer: String
    */

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
    <td><a href={s"https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=${row.series_id}"} target="_blank">{row.series_id}</a></td>
    <td>{row.organism_ch1}</td>
    <td>{row.characteristics_ch1}</td>
    <td>{row.source_name_ch1}</td>
    <td>{row.sequencer}</td>
    <td>{row.molecule_ch1}</td>
    <td>{row.extract_protocol_ch1}</td>
    <td>{row.description}</td>
    <td>{row.data_processing}</td>
    <td>{row.treatment_protocol_ch1}</td>
    <td>{row.submission_date}</td>
    <td>{row.last_update_date}</td>
    <td>{row.`type`}</td>
    <td><a href={s"https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=${row.gpl}"} target="_blank">{row.gpl}</a></td>
    <td>{row.contact}</td>
    </tr>

}
