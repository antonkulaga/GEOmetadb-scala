package group.research.aging.geometa.original

import group.research.aging.geometa.core.{QueryBuilder, QueryRunner}

import scala.collection.immutable._
//import wvlet.log.{LogLevel, LogSupport, Logger}

import cats.implicits._
import doobie._
import doobie.implicits._

trait BasicGEO extends QueryRunner{

  val builder = new QueryBuilderGEO
  //import builder._

  lazy val technology: String = "high-throughput sequencing"
  lazy val sequencingTech = Fragments.and(fr"gsm.gpl = gpl.gpl", fr"gpl.technology = ${technology}")

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


  lazy val modelOrganisms = builder.withSpecies(List("Homo sapiens",
    "Mus musculus","Drosophila melanogaster",
    "Heterocephalus glaber", "Caenorhabditis elegans"))

  def allBy(field: Fragment) = {
    sql"SELECT DISTINCT " ++ field ++ fr"FROM gsm, gpl " ++ Fragments.whereAndOpt(
      Some(sequencingTech)
    ) ++ fr"ORDER BY" ++ field
  }
}