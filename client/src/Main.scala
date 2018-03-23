package client
import scala.scalajs._
import scala.scalajs.js.annotation._

import shared.Hello
import org.scalajs.dom
import org.scalajs.dom.html

@JSExportTopLevel("MainJS")
object MainJS {

  @JSExport
  def helloJs = Hello.sayHello("js")
  //def blank = dom.document.getElementById("blank")
  val child = dom.document.createElement("div")
  child.textContent = Hello.sayHello("js")
  //blank.appendChild(child)
}
