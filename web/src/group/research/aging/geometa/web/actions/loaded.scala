package group.research.aging.geometa.web.actions

import group.research.aging.geometa.models.Sequencing
import group.research.aging.geometa.web.states.SamplesQueryInfo
import io.circe.generic.JsonCodec

trait Loaded extends Action

case object NothingLoaded extends Loaded

@JsonCodec case class LoadedPage(
  page: String,
  headers: List[String],
  data: List[List[String]]
)

object LoadedSequencing {
  lazy val empty = LoadedSequencing(SamplesQueryInfo.empty, Nil, 0 , 0)
}
@JsonCodec case class LoadedSequencing(
                                        queryInfo: SamplesQueryInfo,
                                        sequencing: List[Sequencing],
                                        limit: Long,
                                        offset: Long
                                      ) extends Loaded