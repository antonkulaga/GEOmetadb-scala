package group.research.aging.geometa.web.actions

// Data type for events coming from the outside world:
trait Action

object LoadPage {
  lazy val test = LoadPage("test")
}

case class LoadAjax(url: String) extends Action

case class LoadSequencing(limit: Long = 30) extends Action


case class LoadPage(name: String) extends Action


//case class LoadedSequencing(data: Sequencing_GSM)