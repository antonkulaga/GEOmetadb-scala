package group.research.aging.geometa.web.controller

import com.typesafe.config.ConfigFactory
import group.research.aging.geometa.{GEOmeta, Sequencing_GSM}
import io.getquill.{Literal, SqliteJdbcContext}
import io.circe.syntax._

object Controller {


  lazy val config = ConfigFactory.load().getConfig("quill-cache").getConfig("sqlite")
  lazy val ctx: SqliteJdbcContext[Literal.type] = new SqliteJdbcContext(Literal, config)
  lazy val db = new GEOmeta(ctx)

  def getSamples(limit: Int) = {
    val gsms = db.sequencing_gsm(30).asJson
    gsms
  }

}
