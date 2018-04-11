package group.research.aging.geometa.web.controller

import cats.effect.IO
import doobie.hikari.HikariTransactor
import group.research.aging.geometa.GEOmeta
import group.research.aging.geometa.web.actions
import group.research.aging.geometa.web.states._
import wvlet.log.LogFormatter.SourceCodeLogFormatter
import wvlet.log.{LogLevel, Logger}


class Controller(transactor: IO[HikariTransactor[IO]]) extends GEOmeta(transactor){

  // Set the default log formatter
  Logger.setDefaultFormatter(SourceCodeLogFormatter)
  Logger.setDefaultLogLevel(LogLevel.DEBUG)


  def loadSequencing(
                      parameters: QueryParameters
                    ) = {
    val gsms = super.sequencing(parameters.species, parameters.limit, parameters.offset)
    val sp = super.all_species()
    val platforms = super.all_sequencers()
    val query = SamplesQueryInfo(sp.toList, platforms.toList) //TODO: fix collections
    actions.LoadedSequencing(query, gsms, limit, offset)
    //actions
  }

}
