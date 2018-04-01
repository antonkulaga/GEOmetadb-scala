package group.research.aging.geometa.web.controller

import com.typesafe.config.ConfigFactory
import group.research.aging.geometa.GEOmeta
import group.research.aging.geometa.models.Sequencing_GSM
import group.research.aging.geometa.web.actions
import io.getquill.{Literal, SqliteJdbcContext}
import shapeless._

object Controller {


  lazy val config = ConfigFactory.load().getConfig("quill-cache").getConfig("sqlite")
  lazy val ctx: SqliteJdbcContext[Literal.type] = new SqliteJdbcContext(Literal, config)
  lazy val db = new GEOmeta(ctx)

  def getSamples(limit: Int = 0, offset: Int = 0) = {
    val gsms = db.sequencing_gsm(30)
    gsms
  }

  def loadSamplesPage(limit: Int = 0, offset: Int = 0) = {
    val gsms = getSamples(limit)
    actions.LoadSequencing(gsms, limit)
    //actions
  }

}
