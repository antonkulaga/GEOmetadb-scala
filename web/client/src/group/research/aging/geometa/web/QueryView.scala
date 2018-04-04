package group.research.aging.geometa.web

import group.research.aging.geometa.web.states.QueryInfo
import mhtml._

class QueryView(queryInfo: Rx[QueryInfo]) extends Base {

  val species: Rx[List[String]] = queryInfo.map(_.species)

  def option(value: String, label: String) = <option value={value}>{label}</option>

  val component = speciesHTML

  lazy val speciesHTML = <select class="ui fluid search dropdown" multiple="true">
    { species.map(ss =>ss.map(s=>option(s,s))) }
    </select>

    /*

  val speciesHTML =
    <select class="ui fluid search dropdown" multiple="">
      { species.map(s=> option(s,s)}
    </select>
    */

  /**
    * val runner: Elem =
    * <div class="ui menu">
    * <section class="item">
    * <button class={ enabledIf("ui primary button", validUpload) } onclick = { runClick _}>Run Workflow</button>
    * </section>
    * <section class="item">
    * <div class="ui labeled input">
    * <div class="ui label">workflow wld</div>
    * <input id ="wdl" onclick="this.value=null;" onchange = { uploadFileHandler(wdlFile) _ } accept=".wdl"  name="wdl" type="file" />
    * </div>
    * </section>
    * <section class="item">
    * <div class="ui labeled input">
    * <div class="ui label">inputs json</div>
    * <input id ="inputs" onclick="this.value=null;" onchange = { uploadFileHandler(inputs) _ } accept=".json" name="inputs" type="file" />
    * </div>
    * </section>
    * <section class="item">
    * <div class="ui labeled input">
    * <div class="ui label">options (optional)</div>
    * <input id ="options" onclick="this.value=null;"  onchange = { uploadFileHandler(options) _ } accept=".json"  name="options" type="file" />
    * </div>
    * </section>
    * </div>
    *
    * val updater: Elem =
    * <div class="ui menu">
    * <section class="item">
    * <div class="ui fluid action input">
    * <div class={enabledIf("ui primary button", validUrl)} onclick={ updateClick _}>Update workflows</div>
    * <input id="url" type="text" placeholder="Enter cromwell URL..."  oninput={ updateHandler _ } value={ url.dropRepeats } />
    * <div class="ui small button" onclick={ localhostClick _ }>To default</div>
    * </div>
    * </section>
    * </div>
    */

}
