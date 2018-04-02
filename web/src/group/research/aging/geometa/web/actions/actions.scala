package group.research.aging.geometa.web.actions

import group.research.aging.geometa.models.Sequencing_GSM
import io.circe.generic.JsonCodec

// Data type for events coming from the outside world:
trait Action

trait ToLoad extends Action

case object NothingToLoad extends ToLoad

@JsonCodec case class LoadAjax(url: String) extends ToLoad


object LoadPage {
  lazy val test = LoadPage("test")
}

@JsonCodec case class LoadPage(name: String) extends ToLoad

