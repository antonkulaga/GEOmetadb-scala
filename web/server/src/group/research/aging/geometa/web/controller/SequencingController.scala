package group.research.aging.geometa.web.controller

import cats.effect.IO
import doobie.hikari.HikariTransactor
import doobie.util.fragment.Fragment
import group.research.aging.geometa.models.Sequencing
import group.research.aging.geometa.web.actions
import wvlet.log.LogFormatter.SourceCodeLogFormatter
import wvlet.log.{LogLevel, Logger}
import doobie._
import doobie.hikari._
import doobie.implicits._
import group.research.aging.geometa.original.WithSQLite
import group.research.aging.geometa.sequencing.GEOmetaSequencing
import group.research.aging.geometa.web.actions.{LoadedSequencing, SuggestionsInfo}

import scala.collection.immutable.{List, Nil}

class SequencingController(transactor: IO[HikariTransactor[IO]]) extends GEOmetaSequencing(transactor) {

  // Set the default log formatter
  Logger.setDefaultFormatter(SourceCodeLogFormatter)
  Logger.setDefaultLogLevel(LogLevel.DEBUG)

  def loadSequencing(
                      parameters: actions.QueryParameters
                    ): LoadedSequencing = {

    val results = sequencing(parameters.species, parameters.molecules, parameters.sequencers,
      parameters.andLikeCharacteristics, parameters.orLikeCharacteristics,
      parameters.series, parameters.limit, parameters.offset)
    LoadedSequencing(SuggestionsInfo.empty, parameters, results)
  }



}
