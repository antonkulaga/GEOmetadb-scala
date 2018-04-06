package group.research.aging.geometa.web.actions

import group.research.aging.geometa.models.Sequencing_GSM
import group.research.aging.geometa.web.states.QueryInfo
import io.circe.generic.JsonCodec

trait Loaded extends Action

case object NothingLoaded extends Loaded

@JsonCodec case class LoadedPage(
  page: String,
  headers: List[String],
  data: List[List[String]]
)

@JsonCodec case class LoadedSequencing(
                                        queryInfo: QueryInfo,
                                        sequencing: List[Sequencing_GSM],
                                        limit: Long,
                                        offset: Long
                                      ) extends Loaded