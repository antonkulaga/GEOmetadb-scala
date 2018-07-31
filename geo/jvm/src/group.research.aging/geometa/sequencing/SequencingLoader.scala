package group.research.aging.geometa.sequencing
import cats.effect.IO
import cats.implicits._
import doobie._
import doobie.hikari.HikariTransactor
import doobie.implicits._
import group.research.aging.geometa.models.Sequencing
import group.research.aging.geometa.core.QueryRunner
import group.research.aging.geometa.original.{GEOmeta, QueryBuilderGEO}
import wvlet.log.LoggingMethods

import scala.collection.immutable.{List, Nil}

class SequencingLoader(val transactor: IO[HikariTransactor[IO]], defaultLimit: Int = 0)  extends QueryRunner {


  val builder = new QueryBuilderSequencing
  //import builder._

  protected def debug(value: AnyRef) = println(value)

  def sequencing( species: List[String] = Nil,
                  molecules: List[String] = Nil,
                  sequencers: List[String] = Nil,
                  andLikeCharacteristics: List[String] = Nil,
                  orLikeCharacteristics: List[String] = Nil,
                  series: List[String] = Nil,
                  limit: Int = 0, offset: Int = 0): List[Sequencing] = {
    val where =  builder.makeWhere(species, molecules, sequencers, andLikeCharacteristics, orLikeCharacteristics, series)
    val q: Fragment = (builder.sampleSelection ++ where ++ builder.limitation(limit, offset))
    debug(q)
    val toRun =  q.query[Sequencing].to[List]
    run( toRun )
  }


  def cleanSequencers(): Int = {
    run(cleanIncrement("sequencers_id_seq"))
    run(cleanQuery("sequencers"))
  }

  def cleanSamples(): Int = {
    run(cleanIncrement("samples_id_seq"))
    run(cleanQuery("samples"))
  }

  def cleanMolecules(): Int = {
    run(cleanIncrement("molecules_id_seq"))
    run(cleanQuery("molecules"))
  }

  def cleanOrganisms(): Int = {
    run(cleanIncrement("organisms_id_seq"))
    run(cleanQuery("organisms"))
  }


  def allSequencers(): List[(Int, String)] = {
    run(sql"""SELECT sequencers.id, sequencers.model FROM sequencers""".query[(Int, String)].to[List])
  }

  def allOrganisms(): List[(Int, String)] = {
    run(sql"""SELECT organisms.id, organisms.name FROM organisms""".query[(Int, String)].to[List])
  }

  def allMolecules(): List[(Int, String)] = {
    run(sql"""SELECT molecules.id, molecules.molecule FROM molecules""".query[(Int, String)].to[List])
  }

}
