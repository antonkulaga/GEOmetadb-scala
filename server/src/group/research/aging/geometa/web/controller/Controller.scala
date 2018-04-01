package group.research.aging.geometa.web.controller

import com.typesafe.config.ConfigFactory
import group.research.aging.geometa.models.Sequencing_GSM
import group.research.aging.geometa.{GEOmeta, Tables}
import group.research.aging.geometa.web.actions
import io.getquill.{Literal, SqliteJdbcContext}
import io.circe.syntax._
import model.persistence.HasId
import shapeless._
import record._
import syntax.singleton._
import record._
import syntax.singleton._
import group.research.aging.geometa.web.states

object Controller {


  lazy val config = ConfigFactory.load().getConfig("quill-cache").getConfig("sqlite")
  lazy val ctx: SqliteJdbcContext[Literal.type] = new SqliteJdbcContext(Literal, config)
  lazy val db = new GEOmeta(ctx)

  def getSamples(limit: Int) = {
    val gsms = db.sequencing_gsm(30)
    gsms
  }

  def loadSamplesPage(limit: Int) = {
    val gsms = getSamples(limit)
    //actions
  }

  protected val gen = Generic[Sequencing_GSM]


}
