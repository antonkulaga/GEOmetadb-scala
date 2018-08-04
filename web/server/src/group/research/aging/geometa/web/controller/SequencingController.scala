package group.research.aging.geometa.web.controller

import cats.effect.IO
import doobie.hikari.HikariTransactor
import group.research.aging.geometa.sequencing.SequencingLoader
import group.research.aging.geometa.web.actions
import group.research.aging.geometa.web.actions.{LoadedSequencing, SuggestionsInfo}
import wvlet.log.LogFormatter.SourceCodeLogFormatter
import wvlet.log.{LogLevel, Logger}

class SequencingController(transactor: IO[HikariTransactor[IO]]) extends SequencingLoader(transactor) {

  // Set the default log formatter
  Logger.setDefaultFormatter(SourceCodeLogFormatter)
  Logger.setDefaultLogLevel(LogLevel.DEBUG)

  lazy val defaultSuggestions = SuggestionsInfo(allOrganisms(), allMolecules(), allSequencers())

  def loadSequencing(
                      parameters: actions.QueryParameters
                    ): LoadedSequencing = {

    val results = sequencing(parameters.species, parameters.molecules, parameters.sequencers,
      parameters.andLikeCharacteristics, parameters.orLikeCharacteristics,
      parameters.series, parameters.limit, parameters.offset)
    LoadedSequencing(defaultSuggestions, parameters, results)
  }



}
