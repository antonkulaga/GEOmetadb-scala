package group.research.aging.geometa.web.controller

import cats.effect.IO
import doobie.hikari.HikariTransactor
import doobie.util.fragment.Fragment
import group.research.aging.geometa.models.Sequencing
import group.research.aging.geometa.{GEOmeta, WithSQLite}
import group.research.aging.geometa.web.actions
import wvlet.log.LogFormatter.SourceCodeLogFormatter
import wvlet.log.{LogLevel, Logger}
import doobie._
import doobie.hikari._
import doobie.implicits._

class Controller(transactor: IO[HikariTransactor[IO]]) extends GEOmeta(transactor) with WithSQLite {

  // Set the default log formatter
  Logger.setDefaultFormatter(SourceCodeLogFormatter)
  Logger.setDefaultLogLevel(LogLevel.DEBUG)

  override protected def makeWhere(
                           species: List[String] = Nil,
                           molecules: List[String] = Nil,
                           sequencers: List[String] = Nil,
                           andLikeCharacteristics: List[String] = Nil,
                           orLikeCharacteristics: List[String] = Nil,
                           limit: Int = 0,
                           offset: Int = 0
                         ): Fragment = {
    Fragments.whereAndOpt(Some(sequencingTech), addSpecies(species),
      not(likesOr(fr"sample.extract_protocol_ch1", List("mRNA", "poly-A"))),
      addMolecule(molecules), likeOrSequencer(sequencers),
      characteristics_and(andLikeCharacteristics), characteristics_or(orLikeCharacteristics))
  }

  def loadSequencing(
                      parameters: actions.QueryParameters
                    ) = {
    val gsms = sequencing(
      species = parameters.species,
      molecules = parameters.molecules,
      sequencers = parameters.sequencers,
      andLikeCharacteristics = parameters.andLikeCharacteristics,
      orLikeCharacteristics = parameters.orLikeCharacteristics,
      limit = parameters.limit,
      offset = parameters.offset)
    val suggestions = actions.SuggestionsInfo.empty//actions.SuggestionsInfo(super.all_species().toList, super.all_sequencers().toList, super.all_molecules().toList) //TODO: fix collections
    actions.LoadedSequencing(suggestions, parameters, gsms)
    //actions
  }

}
