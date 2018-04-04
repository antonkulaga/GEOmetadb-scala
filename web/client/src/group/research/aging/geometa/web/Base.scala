package group.research.aging.geometa.web

import wvlet.log.LogSupport

trait Base extends LogSupport{

  type Reducer = PartialFunction[(states.State, actions.Action), states.State]


  def un(str: String) = scala.xml.Unparsed(str)

}
