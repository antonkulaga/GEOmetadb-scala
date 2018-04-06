package group.research.aging.geometa.web

import group.research.aging.geometa.web.actions.{Search, ToLoad}
import group.research.aging.geometa.web.states.QueryInfo
import mhtml._

import scala.scalajs.js

class QueryView(queryInfo: Rx[QueryInfo], toLoad: Var[ToLoad]) extends Base {

  val species: Rx[List[String]] = queryInfo.map(_.species)
  val platforms: Rx[List[String]] = queryInfo.map(_.platforms)

  val updateFilters = species.merge(platforms)

  val detectEndpoints = """$('.ui.search').search({type: 'category'});"""

  val _ = updateFilters.map(_=> js.eval(detectEndpoints))

  def option(value: String, label: String) = <option value={value}>{label}</option>

  val speciesToChoose = species.map(_.nonEmpty)
  val platformsToChoose = platforms.map(_.nonEmpty)


  val component = <div class="ui menu">
      <section class="item">
        Species
      </section>
      <section class="item">
        { speciesHTML }
      </section>
      <section class="item">
        Platforms
      </section>
      <section class="item">
        { platformsHTML }
      </section>
      <button class={ enabledIf("ui primary button", speciesToChoose) } onclick = { updateClick(toLoad, Search.empty) }>Search</button>
    </div>

  lazy val speciesHTML = <select class="ui fluid search dropdown" multiple="true">
    { species.map(ss =>ss.map(s=>option(s,s))) }
    </select>

  lazy val platformsHTML = <select class="ui fluid search dropdown" multiple="true">
    { platforms.map(ss =>ss.map(s=>option(s,s))) }
  </select>
}
