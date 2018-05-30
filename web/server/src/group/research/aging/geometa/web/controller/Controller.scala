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

import scala.collection.immutable.{List, Nil}

class Controller(transactor: IO[HikariTransactor[IO]]) extends GEOmeta(transactor) with WithSQLite {

  // Set the default log formatter
  Logger.setDefaultFormatter(SourceCodeLogFormatter)
  Logger.setDefaultLogLevel(LogLevel.DEBUG)

  def loadSequencing(
                      parameters: actions.QueryParameters
                    ) = {
    val gsms = sequencing(
      species = parameters.species,
      molecules = parameters.molecules,
      sequencers = parameters.sequencers,
      andLikeCharacteristics = parameters.andLikeCharacteristics,
      orLikeCharacteristics = parameters.orLikeCharacteristics,
      series = parameters.series,
      limit = parameters.limit,
      offset = parameters.offset)
    val suggestions = actions.SuggestionsInfo.empty//actions.SuggestionsInfo(super.all_species().toList, super.all_sequencers().toList, super.all_molecules().toList) //TODO: fix collections
    actions.LoadedSequencing(suggestions, parameters, gsms)
    //actions
  }

}
