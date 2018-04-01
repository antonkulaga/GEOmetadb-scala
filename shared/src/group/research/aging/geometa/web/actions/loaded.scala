package group.research.aging.geometa.web.actions

import io.circe.generic.JsonCodec

trait Loaded extends Action

case object NothingLoaded extends Loaded

@JsonCodec case class LoadedPage(
  page: String, headers: List[String], data: List[List[String]]

)