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

trait BasicGEO{

  def transactor: IO[HikariTransactor[IO]]

  def run[T](q: doobie.ConnectionIO[T]): T =
    (for{ xa <- transactor ; selection <- q.transact(xa)} yield selection).unsafeRunSync

  def debug[T](q: Query0[T])=
    {
      transactor.flatMap{ xa =>
        val y = xa.yolo
        import y._
        q.check
      }
    }.unsafeRunSync()

  lazy val technology: String = "high-throughput sequencing"
  lazy val sequencingTech = Fragments.and(fr"sample.gpl = gpl.gpl", fr"gpl.technology = ${technology}")

  protected def addOpt(fieldName: Fragment, value: Option[String]) = value.map(sp=> fr" UPPER(" ++ fieldName ++ fr") = ${sp.toUpperCase}")

  protected def addInOpt(fieldName: Fragment, values: List[String]): Option[doobie.Fragment] = if(values.size > 1)
  {
    val frag: Fragment = fr" UPPER(" ++ fieldName ++ fr")"
    values.toNel.map(v => Fragments.in(frag, v.map(_.toUpperCase)))
  } else addOpt(fieldName, values.headOption)

  def not(fragment: Option[Fragment]): Option[doobie.Fragment] = fragment.map(frag=> fr"NOT ("++ frag ++ fr")")

  protected def addSpecies(values: List[String]) = addInOpt(fr"sample.organism_ch1", values)
  protected def notInExtraction(values: List[String]) = addInOpt(fr"sample.extract_protocol_ch1", values)
  protected def addMolecule(values: List[String]) = addInOpt(fr"sample.molecule_ch1", values)


  protected def addSequencer(values: List[String]) = addInOpt(fr"gpl.title", values)
  //protected def likeAndSequencer(values: List[String]): Option[doobie.Fragment] = likesAdd(fr"gpl.title", values)
  protected def likeOrSequencer(values: List[String]): Option[doobie.Fragment] = likesOr(fr"gpl.title", values)

  protected def likeInside(fieldName: Fragment, values: List[String], upper: Boolean) = {
    val f = if(upper) fr"UPPER("++fieldName ++ fr")" else fieldName
    val cased = values.map(v => if(upper)  "%" + v.toUpperCase + "%" else "%" + v + "%")
    cased.map(v=> f ++ fr"LIKE ${v}")
  }

  protected def likesAdd(fieldName: Fragment, values: List[String], upper: Boolean = true): Option[doobie.Fragment] = if(values.isEmpty) None else {
    Some(fr"(" ++Fragments.and(likeInside(fieldName, values, upper):_*) ++ fr")")
  }

  protected def likesOr(fieldName: Fragment, values: List[String], upper: Boolean = true): Option[doobie.Fragment] = if(values.isEmpty) None else {
    Some(fr"(" ++ Fragments.or(likeInside(fieldName, values, upper):_*) ++ fr")")
  }

  protected def characteristics_and(values: List[String], upper: Boolean = true) = {
    likesAdd(fr"sample.characteristics_ch1", values, upper)
  }

  protected def characteristics_or(values: List[String], upper: Boolean = true) = {
    likesOr(fr"sample.characteristics_ch1", values, upper)
  }

  protected def limitation(limit: Int = 0, offset: Int = 0) = if(limit <= 0) fr"" else if(offset <= 0) fr"LIMIT $limit" else fr"LIMIT $limit OFFSET ${offset}"

  val sampleSelection =
    sql"""SELECT sample.ID, sample.title, sample.gsm, sample.series_id, sample.gpl, sample.status,
          sample.submission_date, sample.last_update_date, sample.type, sample.source_name_ch1,
          sample.organism_ch1, sample.characteristics_ch1, sample.molecule_ch1,
          sample.treatment_protocol_ch1, sample.extract_protocol_ch1,
          sample.description, sample.data_processing, sample.contact,
          sample.data_row_count, sample.channel_count, gpl.title
        FROM gsm sample, gpl """

  val sampleCount =
    sql"""SELECT count(*)
        FROM gsm sample, gpl """


  lazy val modelOrganisms = addSpecies(List("Homo sapiens",
    "Mus musculus","Drosophila melanogaster",
    "Heterocephalus glaber", "Caenorhabditis elegans"))

  def allBy(field: Fragment) = {
    sql"SELECT DISTINCT " ++ field ++ fr"FROM gsm sample, gpl " ++ Fragments.whereAndOpt(
      Some(sequencingTech)
    ) ++ fr"ORDER BY" ++ field
  }
}