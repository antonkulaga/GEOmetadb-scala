package group.research.aging.geometa

import shapeless._

import scala.collection.immutable._
import group.research.aging.geometa.models._
import wvlet.log.LogSupport
//import wvlet.log.{LogLevel, LogSupport, Logger}

import doobie._
import doobie.implicits._
import cats.implicits._
import cats.effect.IO
import pprint.PPrinter.BlackWhite
import doobie.hikari._
import doobie.hikari.implicits._


class GEOmeta(val transactor: IO[HikariTransactor[IO]]) extends BasicGEO with LogSupport
{
  val whereMitya = Fragments.whereAndOpt(
    Some(sequencingTech),
    addSpecies(List("Mus musculus", "Homo sapiens", "Bos taurus")),
    addMolecule(List("total RNA")),
    likeAndSequencer(List("llumina HiSeq 2500")),
    characteristics_and(List("age", "tissue")),
    characteristics_or(List("liver", "kidney"))
  )


  def ll sequencing(species: List[String] = Nil,
                 molecules: List[String] = Nil,
                 limit: Int = 0, offset: Int = 0)= {
    sampleSelection ++ whereMitya ++ limitation(10)
  }

  protected def get_sequencer(n: String): String =  n.indexOf(" (") match {
    case -1 => n.toLowerCase
    case v => n.substring(0, v).toLowerCase
  }

  def all_sequencers() = {
    val q: doobie.ConnectionIO[List[String]] =
      sql"""SELECT DISTINCT gpl.title
        FROM gpl
        WHERE AND gpl.technology = '${technology}'
        ;""".query[String].to[List]
    val list = run(q).map(get_sequencer)
    SortedSet(list:_*)
  }


  def all_molecules() = {
    val q: doobie.ConnectionIO[List[String]] =
      sql"""SELECT DISTINCT sample.molecule_ch1
        FROM gsm sample, gpl
        WHERE sample.gpl = gpl.gpl AND gpl.technology = '${technology}'
        ;""".query[String].to[List]
    val list = run(q)
    SortedSet(list:_*)
  }

  def all_species() = {
    val q: doobie.ConnectionIO[List[String]] =
      sql"""SELECT DISTINCT gpl.organism
        FROM gpl
        WHERE AND gpl.technology = '${technology}'
        ;""".query[String].to[List]
    val list = run(q).flatMap(s=>s.split("\t").map(_.trim)).distinct
    SortedSet(list:_*)
  }

  /*
  def sequencing(limit: Int = 0, offset: Int = 0)= {
    val q = context.quote{
      for {
        sample <- query[Tables.gsm]
        gpl <- query[Tables.gpl]
        if sample.gpl == gpl.gpl
        if gpl.technology == lift(technology)
      } yield { (sample, gpl.title) }
    }
    val results = if(limit > 0) context.run(q.drop(lift(offset)).take(lift(limit))) else context.run(q)
    results.map{ case (sample, title) => Sequencing.fromGSM(sample, get_sequencer(title))}
  }




  def gsm(limit: Int = 0, offset: Int = 0): List[Tables.gsm] = {
    val q = context.quote{
      query[Tables.gsm]
    }
    if(limit > 0) context.run(q.drop(lift(offset)).take(lift(limit))) else context.run(q)
  }

  def get_sequencer(n: String): String =  n.indexOf(" (") match {
    case -1 => n.toLowerCase
    case v => n.substring(0, v).toLowerCase
  }

  def all_species() = {
    val q = context.quote{ query[Tables.gpl].filter(g=>g.technology == lift(technology)).map(_.organism).distinct}
    //context.run(q).toList
    /*
    val q = context.quote{
      for {
        sample <- query[Tables.gsm]
        gpl <- query[Tables.gpl]
        if sample.gpl == gpl.gpl
        if gpl.technology == lift(technology)
      } yield { sample.organism_ch1 }
    }
    */
    context.run(q).toList
  }

  def all_molecules() = {
    val q = context.quote{
      for {
        sample <- query[Tables.gsm]
        gpl <- query[Tables.gpl]
        if sample.gpl == gpl.gpl
        if gpl.technology == lift(technology)
      } yield { sample.molecule_ch1 }
    }
    context.run(q.distinct).toList
  }

  def complexSearch() = {
    val q = context.quote {
      infix"""SELECT sample.ID, sample.title, sample.gsm, sample.series_id, sample.gpl, sample.status,
              sample.submission_date, sample.last_update_date, sample.type, sample.source_name_ch1,
              sample.organism_ch1, sample.characteristics_ch1, sample.molecule_ch1, sample.label_ch1,
              sample.treatment_protocol_ch1, sample.extract_protocol_ch1, sample.label_protocol_ch1,
              sample.source_name_ch2, sample.organism_ch2, sample.characteristics_ch2, sample.molecule_ch2,
              sample.label_ch2, sample.treatment_protocol_ch2, sample.extract_protocol_ch2, sample.label_protocol_ch2,
              sample.hyb_protocol, sample.description, sample.data_processing, sample.contact, sample.supplementary_file,
              sample.data_row_count, sample.channel_count, gpl.title
           FROM gsm sample, gpl gpl
           WHERE sample.gpl = gpl.gpl AND gpl.technology = ${technology}"""
    }
  }


  def gpl(limit: Int = 0, offset: Int = 0): List[Tables.gpl] = {
    val q = context.quote{
      query[Tables.gpl].filter(g=>g.technology == lift(technology))
    }
    if(limit > 0) context.run(q.drop(lift(offset)).take(lift(limit))) else context.run(q)
  }
  */
}