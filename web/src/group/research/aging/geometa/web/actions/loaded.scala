package group.research.aging.geometa.web.actions

import group.research.aging.geometa.models.Sequencing
import io.circe.generic.JsonCodec

import scala.collection.immutable._

object SuggestionsInfo{
  lazy val empty = SuggestionsInfo(List.empty, List.empty, List.empty)
}
@JsonCodec case class SuggestionsInfo(
                                       species: List[(Int, String)],
                                       molecules: List[(Int, String)],
                                       sequencers: List[(Int, String)],
                                     ) extends Action
object QueryParameters {
  lazy val empty = QueryParameters()

  lazy val test = QueryParameters(
    species = List("Mus musculus", "Bos taurus"),
    molecules = List("total RNA"),
    sequencers = List("HiSeq", "NovaSeq"),
    andLikeCharacteristics = List("age"),
    orLikeCharacteristics = List("kidney", "liver"),
    limit = 500
  )

  lazy val mus = QueryParameters(
    species = List("Mus musculus"),
    molecules = List("total RNA"),
    sequencers = List("HiSeq", "NovaSeq"),
    andLikeCharacteristics = List("age", "tissue"),
    orLikeCharacteristics = List("liver", "kidney"),
    limit = 1000
  )

  lazy val rna = QueryParameters(
    molecules = List("total RNA", "mRNA"),
   limit = 10000
  )


  lazy val taurus = QueryParameters(
    species = List("Bos taurus"),
    molecules = List("total RNA"),
    sequencers = List("HiSeq", "NovaSeq"),
    andLikeCharacteristics = List("tissue"),
    orLikeCharacteristics = List("kidney", "liver"),
    limit =  1000
  )

}
@JsonCodec case class QueryParameters(
                                       species: List[String] = Nil,
                                       molecules: List[String] = Nil,
                                       sequencers: List[String] = Nil,
                                       andLikeCharacteristics: List[String] = Nil,
                                       orLikeCharacteristics: List[String] = Nil,
                                       series: List[String] = Nil,
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
