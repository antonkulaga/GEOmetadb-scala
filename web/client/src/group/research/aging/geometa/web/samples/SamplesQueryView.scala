package group.research.aging.geometa.web.samples

import group.research.aging.geometa.web.Base
import group.research.aging.geometa.web.actions
import mhtml._

import scala.scalajs.js

class SamplesQueryView(suggestions: Rx[actions.SuggestionsInfo], toLoad: Var[actions.ToLoad], updateUI: Var[actions.UpdateUI]) extends Base {

  val species: Rx[List[String]] = suggestions.map(_.species)
  val sequencer: Rx[List[String]] = suggestions.map(_.sequencers)
  val molecules = suggestions.map(_.molecules)

  val updateFilters = species.merge(sequencer)

  def option(value: String, label: String) = <option value={value}>{label}</option>

  val speciesToChoose = species.map(_.nonEmpty)
  val platformsToChoose = sequencer.map(_.nonEmpty)

  lazy val speciesHTML = <select class="ui fluid search dropdown" multiple="true">
    { species.map(ss =>ss.map(s=>option(s,s))) }
  </select>

  lazy val platformsHTML = <select class="ui fluid search dropdown" multiple="true">
    { sequencer.map(ss =>ss.map(s=>option(s,s))) }
  </select>

  lazy val moleculesHTML = <select class="ui fluid search dropdown" multiple="true">
    { molecules.map(ss =>ss.map(s=>option(s,s))) }
  </select>

  lazy val labels =
      <tr>
        <th>Title</th>
        <th>GSM</th>
        <th>GSE</th>
        <th class="two wide">Species</th>
        <th class="three wide">Characteristics</th>
        <th>Source</th>
        <th>Sequencer</th>
        <th>Extracted Molecule</th>
        <th class="three wide">Extraction Protocol</th>
        <th>Description</th>
        <th class="two wide">Proccesing</th>
        <th>Treatment Protocol</th>
        <th>Submission</th>
        <th>Last Update</th>
        <th>Type</th>
        <th>GPL</th>
        <th colspan="2">Contact</th>
      </tr>

  lazy val jsUpdate = """$('.ui.dropdown').dropdown();"""

  lazy val queries =
      <tr>
        <th></th>
        <th></th>
        <th></th>
        <th>{ speciesHTML }</th>
        <th></th>
        <th></th>
        <th>{ platformsHTML }</th>
        <th>{ moleculesHTML }</th>
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
      {suggestions.map{ q=>
        if(q!=actions.SuggestionsInfo.empty) updateUI := actions.EvalJS(jsUpdate)
        "" //trying to trigger update
      }}
    </thead>

//<button class={ enabledIf("ui primary button", speciesToChoose) } onclick = { updateClick(toLoad, Search.empty) }>Search</button>

}
