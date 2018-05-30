package group.research.aging.geometa

import group.research.aging.geometa.models._
import wvlet.log.LogSupport

import scala.collection.immutable._
//import wvlet.log.{LogLevel, LogSupport, Logger}

import cats.effect.IO
import cats.implicits._
import doobie._
import doobie.hikari._
import doobie.implicits._


class GEOmeta(val transactor: IO[HikariTransactor[IO]]) extends BasicGEO with LogSupport
{

  protected def makeWhere(
                           species: List[String] = Nil,
                           molecules: List[String] = Nil,
                           sequencers: List[String] = Nil,
                           andLikeCharacteristics: List[String] = Nil,
                           orLikeCharacteristics: List[String] = Nil,
                           series: List[String] = Nil,
                           limit: Int = 0,
                           offset: Int = 0
                         ): Fragment =
    Fragments.whereAndOpt(       Some(sequencingTech), addSpecies(species),
      addMolecule(molecules), likeOrSequencer(sequencers),
      characteristics_and(andLikeCharacteristics), characteristics_or(orLikeCharacteristics), this.addSeries(series))

  /**
    * Loads sequencing with filtering parameters
    * @param species
    * @param molecules
    * @param sequencers
    * @param andLikeCharacteristics
    * @param orLikeCharacteristics
    * @param limit
    * @param offset
    * @return
    */
  def sequencing( species: List[String] = Nil,
                  molecules: List[String] = Nil,
                  sequencers: List[String] = Nil,
                  andLikeCharacteristics: List[String] = Nil,
                  orLikeCharacteristics: List[String] = Nil,
                  series: List[String] = Nil,
                 limit: Int = 0, offset: Int = 0) = {
    val where =  makeWhere(species, molecules, sequencers, andLikeCharacteristics, orLikeCharacteristics, series)
    val q = (sampleSelection ++ where ++ limitation(limit, offset)).query[Sequencing]
    run( q.to[List])
  }

  protected def get_sequencer(n: String): String =  n.indexOf(" (") match {
    case -1 => n.toLowerCase
    case v => n.substring(0, v).toLowerCase
  }


  def all_sequencers() = {
    val q: doobie.ConnectionIO[List[String]] =
      sql"""SELECT DISTINCT gpl.title
        FROM gpl
        WHERE AND gpl.technology = ${technology}
        ;""".query[String].to[List]
    val list = run(q).map(get_sequencer)
    SortedSet(list:_*)
  }


  def all_molecules() = run(allBy(fr"sample.molecule_ch1").query[String].to[List])
  def all_species() = run(allBy(fr"gpl.organism").query[String].to[List]).flatMap(s=>s.split("\t").map(_.trim)).distinct
  
}