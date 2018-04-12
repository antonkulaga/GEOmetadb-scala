package group.research.aging.geometa.web.controller

import cats.effect.IO
import doobie.hikari.HikariTransactor
import group.research.aging.geometa.{GEOmeta, WithSQLite}
import group.research.aging.geometa.web.actions
import wvlet.log.LogFormatter.SourceCodeLogFormatter
import wvlet.log.{LogLevel, Logger}


class Controller(transactor: IO[HikariTransactor[IO]]) extends GEOmeta(transactor) with WithSQLite {

  // Set the default log formatter
  Logger.setDefaultFormatter(SourceCodeLogFormatter)
  Logger.setDefaultLogLevel(LogLevel.DEBUG)


  def loadSequencing(
                      parameters: actions.QueryParameters
                    ) = {
    val gsms = super.sequencing(
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
