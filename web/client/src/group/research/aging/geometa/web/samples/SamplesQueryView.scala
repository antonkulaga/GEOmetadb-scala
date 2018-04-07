package group.research.aging.geometa.web.samples

import group.research.aging.geometa.web.Base
import group.research.aging.geometa.web.actions
import group.research.aging.geometa.web.states.SamplesQueryInfo
import mhtml._

import scala.scalajs.js

class SamplesQueryView(queryInfo: Rx[SamplesQueryInfo], toLoad: Var[actions.ToLoad], updateUI: Var[actions.UpdateUI]) extends Base {

  val species: Rx[List[String]] = queryInfo.map(_.species)
  val platforms: Rx[List[String]] = queryInfo.map(_.platforms)

  val updateFilters = species.merge(platforms)

  def option(value: String, label: String) = <option value={value}>{label}</option>

  val speciesToChoose = species.map(_.nonEmpty)
  val platformsToChoose = platforms.map(_.nonEmpty)

  lazy val speciesHTML = <select class="ui fluid search dropdown" multiple="true">
    { species.map(ss =>ss.map(s=>option(s,s))) }
  </select>

  lazy val platformsHTML = <select class="ui fluid search dropdown" multiple="true">
    { platforms.map(ss =>ss.map(s=>option(s,s))) }
  </select>


  lazy val labels =
      <tr>
        <th>Title</th>
        <th>GSM</th>
        <th>GSE</th>
        <th>Species</th>
        <th>Characteristics</th>
        <th>Source</th>
        <th>Sequencer</th>
        <th>Extracted Molecule</th>
        <th>Extraction Protocol</th>
        <th>Description</th>
        <th>Proccesing</th>
        <th>Treatment Protocol</th>
        <th>Submission</th>
        <th>Last Update</th>
        <th>Type</th>
        <th>GPL</th>
        <th colspan="2">Contact</th>
      </tr>

  lazy val jsUpdate = """$('.ui.dropdown').dropdown();"""

  lazy val queries =
      <tr onload={this.updateClick(updateUI, actions.EvalJS(jsUpdate))}>
        <th>{ speciesHTML }</th>
        <th>{ platformsHTML }</th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th colspan="2"></th>
      </tr>

  val component =
    <thead>
      { labels }
      { queries }
    </thead>

//<button class={ enabledIf("ui primary button", speciesToChoose) } onclick = { updateClick(toLoad, Search.empty) }>Search</button>

}
