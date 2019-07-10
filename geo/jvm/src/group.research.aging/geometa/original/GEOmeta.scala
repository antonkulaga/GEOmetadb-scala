package group.research.aging.geometa.original

import group.research.aging.geometa.models._
import wvlet.log.LogSupport

import scala.collection.immutable._
//import wvlet.log.{LogLevel, LogSupport, Logger}

import cats.effect.IO
import cats.implicits._
import doobie._
import doobie.hikari._
import doobie.implicits._


class GEOmeta(val transactor: Transactor.Aux[IO, Unit]) extends BasicGEO with LogSupport
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
    Fragments.whereAndOpt(       Some(sequencingTech), builder.withSpecies(species),
      builder.addMolecule(molecules), builder.likeOrSequencer(sequencers),
      builder.characteristics_and(andLikeCharacteristics), builder.characteristics_or(orLikeCharacteristics), builder.withSeries(series))

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
                 limit: Int = 0, offset: Int = 0): List[SequencingGEO] = {
    val where =  makeWhere(species, molecules, sequencers, andLikeCharacteristics, orLikeCharacteristics, series)
    val q = (sampleSelection ++ where ++ builder.limitation(limit, offset)).query[SequencingGEO]
    run( q.to[List])
  }

  protected def get_sequencer(n: String): String =  n.indexOf(" (") match {
    case -1 => n.toLowerCase
    case v => n.substring(0, v).toLowerCase
  }

  def gpl2sequencer() = {
    val q: doobie.ConnectionIO[List[String]] =
      sql"""SELECT DISTINCT gpl.ID, gpl.gpl.title, gpl.gpl
        FROM gpl
        WHERE gpl.technology = ${technology}
        ;""".query[String].to[List]
    q
  }

  def all_sequencers(): SortedSet[String] = {
    val q =
      sql"""SELECT DISTINCT gpl.title
        FROM gpl
        WHERE gpl.technology = ${technology}
        ;""".query[String].to[SortedSet]
    run(q).map(get_sequencer)
  }

  def all_molecules(): SortedSet[String] = run(allBy(fr"gsm.molecule_ch1").query[String].to[SortedSet])
  def all_species(): SortedSet[String] = run(allBy(fr"gpl.organism").query[String]
    .to[SortedSet])
    .flatMap(s=>s.split("\t")
      .map(_.replace(";", "").trim)
    )
  
}