package group.research.aging.geometa.web.actions

import group.research.aging.geometa.models.Sequencing
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

object Search{
  lazy val empty = Search(Map.empty)
}
@JsonCodec case class Search(parameters: Map[String, String]) extends ToLoad

//case class Error(throwable: Throwable) extends Action

object ExplainedError {
  lazy val empty = ExplainedError("", "")
}
@JsonCodec case class ExplainedError(message: String, errorMessage: String) extends Action
