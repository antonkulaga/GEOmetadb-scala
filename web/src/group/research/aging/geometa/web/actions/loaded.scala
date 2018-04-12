package group.research.aging.geometa.web.actions

import group.research.aging.geometa.models.Sequencing
import io.circe.generic.JsonCodec

import scala.collection.immutable._

object SuggestionsInfo{
  lazy val empty = SuggestionsInfo(Nil, Nil)
}
@JsonCodec case class SuggestionsInfo(
                                       species: List[String],
                                       molecules: List[String] = Nil,
                                       sequencers: List[String] = Nil,
                                     ) extends Action
object QueryParameters {
  lazy val empty = QueryParameters()

  lazy val test = QueryParameters(
    species = List("Mus musculus", "Bos taurus"),
    molecules = List("total RNA"),
    andLikeCharacteristics = List("age"),
    orLikeCharacteristics = List("kidney", "liver"),
    limit = 50
  )

}
@JsonCodec case class QueryParameters(
                                       species: List[String] = Nil,
                                       molecules: List[String] = Nil,
                                       sequencers: List[String] = Nil,
                                       andLikeCharacteristics: List[String] = Nil,
                                       orLikeCharacteristics: List[String] = Nil,
                                       limit: Int = 50,
                                       offset: Int = 0
                                     ) extends Action

trait Loaded extends Action

case object NothingLoaded extends Loaded

@JsonCodec case class LoadedPage(
  page: String,
  headers: List[String],
  data: List[List[String]]
)

object LoadedSequencing {
  lazy val empty = LoadedSequencing(SuggestionsInfo.empty, QueryParameters.empty, Nil)
}
@JsonCodec case class LoadedSequencing(
                                        suggestionInfo: SuggestionsInfo,
                                        queryParameters: QueryParameters,
                                        sequencing: List[Sequencing]
                                      ) extends Loaded
