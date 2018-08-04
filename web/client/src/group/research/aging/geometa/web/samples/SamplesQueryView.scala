package group.research.aging.geometa.web.samples

import group.research.aging.geometa.web.Base
import group.research.aging.geometa.web.actions
import mhtml._

import scala.scalajs.js
import scala.xml.Elem

class SamplesQueryView(suggestions: Rx[actions.SuggestionsInfo], toLoad: Var[actions.ToLoad], updateUI: Var[actions.UpdateUI]) extends Base {

  val species:   Rx[List[(Int,String)]] = suggestions.map(_.species)
  val sequencer: Rx[List[(Int,String)]] = suggestions.map(_.sequencers)
  val molecules: Rx[List[(Int,String)]] = suggestions.map(_.molecules)

  val updateFilters = species.merge(sequencer)

  def option(value: String, label: String): Elem = <option value={value}>{label}</option>

  val speciesToChoose: Rx[Boolean] = species.map(_.nonEmpty)
  val platformsToChoose = sequencer.map(_.nonEmpty)

  lazy val speciesHTML = <select class="ui fluid search dropdown" multiple="true">
    { species.map(ss =>ss.map{ case (k, v)=>option(k.toString,v) }) }

  </select>

  lazy val platformsHTML = <select class="ui fluid search dropdown" multiple="true">
    { sequencer.map(ss =>ss.map{ case (k, v)=> option(k.toString,v) }) }
  </select>

  lazy val moleculesHTML = <select class="ui fluid search dropdown" multiple="true">
    { molecules.map(ss =>ss.map{ case (k, v)=> option(k.toString,v) }) }
  </select>


  lazy val labels =
      <tr>
        <th>Title</th>
        <th>GSM</th>
        <th>GSE</th>
        <th class="two wide">Species</th>
        <th>Extracted Molecule</th>
        <th>Sequencer</th>
        <th class="three wide">Characteristics</th>
        <th class="two wide">Description</th>
        <th class="two wide">Treatment Protocol</th>
        <th class="three wide">Extraction Protocol</th>
        <th>Source</th>
        <th class="two wide">Proccesing</th>
        <th>Submission</th>
        <th>Last Update</th>
        <th>Status</th>
        <th>Type</th>
        <th class="two wide">Contact</th>
        <th>GPL</th>
      </tr>

  lazy val jsUpdate = """$('.ui.dropdown').dropdown();"""

  lazy val queries =
      <tr>
        <th></th>
        <th></th>
        <th></th>
        <th class="two wide">{ speciesHTML }</th>
        <th>{ moleculesHTML }</th>
        <th>{ platformsHTML }</th>
        <th class="three wide"></th>
        <th class="two wide"></th>
        <th class="two wide"></th>
        <th class="three wide"></th>
        <th></th>
        <th class="two wide"></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th class="two wide"></th>
        <th></th>
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
