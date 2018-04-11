package group.research.aging.geometa.web.actions

import group.research.aging.geometa.models.Sequencing
import group.research.aging.geometa.web.states.SuggestionsInfo
import io.circe.generic.JsonCodec

trait Loaded extends Action

case object NothingLoaded extends Loaded

@JsonCodec case class LoadedPage(
  page: String,
  headers: List[String],
  data: List[List[String]]
)

object LoadedSequencing {
  lazy val empty = LoadedSequencing(SuggestionsInfo.empty, Nil, 0 , 0)
}
@JsonCodec case class LoadedSequencing(
                                        queryInfo: SuggestionsInfo,
                                        sequencing: List[Sequencing],
                                        limit: Long,
                                        offset: Long
                                      ) extends Loaded