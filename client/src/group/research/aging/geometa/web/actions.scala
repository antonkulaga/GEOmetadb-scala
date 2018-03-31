package group.research.aging.geometa.web

// Data type for events coming from the outside world:
trait Action

object LoadPage {
  lazy val test = LoadPage("test")
}
case class LoadPage(name: String)
case class LoadSequencing(limit: Long = 30)
//case class LoadedSequencing(data: Sequencing_GSM)