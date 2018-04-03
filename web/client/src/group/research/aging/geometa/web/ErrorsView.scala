package group.research.aging.geometa.web

import group.research.aging.geometa.web.actions.ExplainedError
import mhtml.{Rx, Var}
import org.scalajs.dom.Event
import wvlet.log.LogSupport

import scala.xml.Elem

class ErrorsView(errors: Rx[List[ExplainedError]]) extends Base{

  protected def updateClick(e: ExplainedError)(event: Event): Unit = {
    //throwError := None
    //    dispatcher.dispatch(Messages.Errors(Nil))
  }

  val component: Rx[List[Elem]] =  errors.map(ee=> ee.map(e=>
    <div class="ui negative message">
      <!--<i class="close icon" onclick={ updateClick _ }></i>-->
      <div class="header">
        {e.message}
      </div>
      <p>{e.errorMessage}</p>
    </div>
    )
  )

}
