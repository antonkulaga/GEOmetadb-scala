package group.research.aging.geometa.web.controller

import com.typesafe.config.ConfigFactory
import group.research.aging.geometa.GEOmeta
import group.research.aging.geometa.models.Sequencing
import group.research.aging.geometa.web.actions
import group.research.aging.geometa.web.states.SamplesQueryInfo
import group.research.aging.utils.SimpleSourceFormatter
import io.getquill.{Literal, SqliteJdbcContext}
import shapeless._
import wvlet.log.LogFormatter.SourceCodeLogFormatter
import wvlet.log.{LogLevel, LogSupport, Logger}

object Controller extends LogSupport{

  // Set the default log formatter
  Logger.setDefaultFormatter(SourceCodeLogFormatter)
  Logger.setDefaultLogLevel(LogLevel.DEBUG)

  lazy val config = ConfigFactory.load().getConfig("quill-cache").getConfig("sqlite")

  lazy val ctx: SqliteJdbcContext[Literal.type] = new SqliteJdbcContext(Literal, config)

  lazy val db = new GEOmeta(ctx)

  def getSamples(limit: Long = 0, offset: Long = 0) = {
    val gsms = db.sequencing(30)
    gsms
  }

  def loadSequencing(limit: Long = 0, offset: Long = 0) = {
    val gsms = getSamples(limit)
    val species = getAllSpecies()
    val platforms = db.all_sequencers()
    val query = SamplesQueryInfo(species, platforms.toList)
    actions.LoadedSequencing(query, gsms, limit, offset)
    //actions
  }

  def bySpecies(species: String) = {
    db.sequencing_by_species(species)
  }

  def getAllSpecies() = {
    db.all_species().flatMap(s=>s.split("\t").map(_.trim)).distinct
  }

  def getAllPlatforms() = {
    db.all_sequencers().toList
  }

}
