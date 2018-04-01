package group.research.aging.geometa.web.actions

import group.research.aging.geometa.models.Sequencing_GSM
import io.circe.generic.JsonCodec

// Data type for events coming from the outside world:
trait Action

object LoadPage {
  lazy val test = LoadPage("test")
}

object ToLoad {
  @JsonCodec case object empty extends ToLoad
}
trait ToLoad extends Action

@JsonCodec case class LoadAjax(url: String) extends ToLoad

@JsonCodec case class LoadPage(name: String) extends ToLoad

@JsonCodec case class LoadSequencing(
                           sequencing: List[Sequencing_GSM],
                           limit: Long,
                           offset: Long
                         ) extends Action

//case class LoadedSequencing(data: Sequencing_GSM)