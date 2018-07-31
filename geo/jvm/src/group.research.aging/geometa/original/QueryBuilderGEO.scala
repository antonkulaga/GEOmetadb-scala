package group.research.aging.geometa.original

import doobie.Fragments
import group.research.aging.geometa.core.QueryBuilder
//import wvlet.log.{LogLevel, LogSupport, Logger}

import doobie.implicits._

import scala.collection.immutable.List

class QueryBuilderGEO extends QueryBuilder{
  lazy val technology: String = "high-throughput sequencing"
  lazy val sequencingTech = Fragments.and(fr"gsm.gpl = gpl.gpl", fr"gpl.technology = ${technology}")

  def withSeries(values: List[String]): Option[doobie.Fragment] = addInOpt(fr"gsm.series_id", values)

  def withSpecies(values: List[String]): Option[doobie.Fragment] = addInOpt(fr"gsm.organism_ch1", values)

  def notInExtraction(values: List[String]) = addInOpt(fr"gsm.extract_protocol_ch1", values)

  def addMolecule(values: List[String]): Option[doobie.Fragment] = addInOpt(fr"gsm.molecule_ch1", values)


  def addSequencer(values: List[String]): Option[doobie.Fragment] = addInOpt(fr"gpl.title", values)
  //def likeAndSequencer(values: List[String]): Option[doobie.Fragment] = likesAdd(fr"gpl.title", values)
  def likeOrSequencer(values: List[String]): Option[doobie.Fragment] = likesOr(fr"gpl.title", values)

  def characteristics_and(values: List[String], upper: Boolean = true) = {
    likesAdd(fr"gsm.characteristics_ch1", values, upper)
  }

  def characteristics_or(values: List[String], upper: Boolean = true) = {
    likesOr(fr"gsm.characteristics_ch1", values, upper)
  }
}
