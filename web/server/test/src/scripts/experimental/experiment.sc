import $exec.dependencies
import dependencies._

import doobie._
import doobie.implicits._
import cats._
import cats.data._
import cats.effect.IO
import cats.implicits._
import pprint.PPrinter.BlackWhite
import doobie.hikari._
import doobie.hikari.implicits._

def sqliteUrl(str: String) = s"jdbc:sqlite:${str}"
val url = sqliteUrl("/pipelines/data/GEOmetadb.sqlite")


implicit val transactor: IO[HikariTransactor[IO]] = HikariTransactor.newHikariTransactor[IO](
    "org.sqlite.JDBC", url, "", ""
  )


val xa = Transactor.fromDriverManager[IO]("org.sqlite.JDBC", url)
val y = xa.yolo
import y._

def run[T](q: doobie.ConnectionIO[T]): T =
  (for{ xa <- transactor ; selection <- q.transact(xa)} yield selection).unsafeRunSync

def debug(q: Query0[Sequencing])=
  {
    transactor.flatMap{ xa =>
      val y = xa.yolo
      import y._
      q.check
    }
  }.unsafeRunSync()

case class Sequencing(
                       ID: String,
                       title: String,
                       gsm: String,
                       series_id: String,
                       gpl: String,
                       status: String,
                       submission_date: String,
                       last_update_date: String,
                       `type`: String,
                       source_name_ch1: String,
                       organism_ch1: String,
                       characteristics_ch1: String,
                       molecule_ch1: String,
                       treatment_protocol_ch1: Option[String],
                       extract_protocol_ch1: String,
                       description: Option[String],
                       data_processing: String,
                       contact: String,
                       data_row_count: Double,
                       channel_count: Double,
                       sequencer: String
                     )
{
  protected def get_sequencer(n: String): String =  n.indexOf(" (") match {
    case -1 => n.toLowerCase
    case v => n.substring(0, v).toLowerCase
  }

  def withFixedSequencer: Sequencing = this.copy(sequencer = get_sequencer(this.sequencer))
}

lazy val technology: String = "high-throughput sequencing"
lazy val sequencingTech = Fragments.and(fr"sample.gpl = gpl.gpl", fr"gpl.technology = ${technology}")

def addOpt(fieldName: Fragment, value: Option[String]) = value.map(sp=> fr" UPPER(" ++ fieldName ++ fr") = ${sp.toUpperCase}")
def addOptSpecies(value: Option[String]) = addOpt(fr"sample.organism_ch1", value)
def limitation(limit: Int = 0, offset: Int = 0) = if(limit <= 0) fr"" else if(offset <= 0) fr"LIMIT $limit" else fr"LIMIT $limit OFFSET ${offset}"


val where = Fragments.whereAndOpt(Some(sequencingTech) , addOptSpecies(Some("Mus musculus")))
/*
val limit = 0
val offset = 0
val sp = if(species == "") fr"" else fr" AND sample.organism_ch1 = '${species}'"
val limitation =  if(limit <= 0) fr"" else
  if(offset <= 0) fr"LIMIT $limit"
    else fr"LIMIT $limit OFFSET ${offset}"
*/

val q: Query0[Sequencing] =
  (sql"""SELECT sample.ID, sample.title, sample.gsm, sample.series_id, sample.gpl, sample.status,
          sample.submission_date, sample.last_update_date, sample.type, sample.source_name_ch1,
          sample.organism_ch1, sample.characteristics_ch1, sample.molecule_ch1,
          sample.treatment_protocol_ch1, sample.extract_protocol_ch1,
          sample.description, sample.data_processing, sample.contact,
          sample.data_row_count, sample.channel_count, gpl.title
        FROM gsm sample, gpl """ ++ where ++ limitation(10)).query[Sequencing]//.to[List]
debug(q)
println("---")

val results = run(q.to[List])
println(results.length)
println("====")
pprint.pprintln(results)
//val r = q.check.runAsync
//debug(q)
//pprint.pprintln(r)
/*
val q: doobie.ConnectionIO[List[Sequencing]] =
  sql"""SELECT sample.ID, sample.title, sample.gsm, sample.series_id, sample.gpl, sample.status,
          sample.submission_date, sample.last_update_date, sample.type, sample.source_name_ch1,
          sample.organism_ch1, sample.characteristics_ch1, sample.molecule_ch1,
          sample.treatment_protocol_ch1, sample.extract_protocol_ch1,
          sample.description, sample.data_processing, sample.contact,
          sample.data_row_count, sample.channel_count, gpl.title
        FROM gsm sample, gpl
        WHERE sample.gpl = gpl.gpl AND gpl.technology = 'high-throughput sequencing' AND
          (UPPER(sample.characteristics_ch1) LIKE '%KIDNEY%' OR UPPER(sample.characteristics_ch1) LIKE '%LIVER%') AND
           UPPER(sample.characteristics_ch1) LIKE '%AGE%' AND UPPER(sample.molecule_ch1) LIKE '%TOTAL RNA%'
  ;""".query[Sequencing].to[List]

def run[T](q: doobie.ConnectionIO[T])
          (implicit transactor: IO[HikariTransactor[IO]]): T =
  (for{ xa <- transactor ; selection <- q.transact(xa)} yield selection).unsafeRunSync

val result: List[Sequencing] = run(q)


//val result: List[Sequencing] = q.transact(xa).unsafeRunSync
BlackWhite.pprintln(result.size)
BlackWhite.pprintln(result)

"""SELECT sample.ID, sample.title, sample.gsm, sample.series_id, sample.gpl, sample.status,
  sample.submission_date, sample.last_update_date, sample.type, sample.source_name_ch1,
  sample.organism_ch1, sample.characteristics_ch1, sample.molecule_ch1, sample.label_ch1,
  sample.treatment_protocol_ch1, sample.extract_protocol_ch1, sample.label_protocol_ch1,
  sample.source_name_ch2, sample.organism_ch2, sample.characteristics_ch2, sample.molecule_ch2,
  sample.label_ch2, sample.treatment_protocol_ch2, sample.extract_protocol_ch2, sample.label_protocol_ch2,
  sample.hyb_protocol, sample.description, sample.data_processing, sample.contact, sample.supplementary_file,
  sample.data_row_count, sample.channel_count, gpl.title
  FROM gsm sample, gpl gpl
  WHERE sample.gpl = gpl.gpl AND gpl.technology = 'high-throughput sequencing' AND gsm = 'GSM2927750'
  """

"""select gse.gse,gse.title, gse.overall_design,gsm.gsm, gsm.title,gsm.organism_ch1,gsm.characteristics_ch1,gsm.molecule_ch1,gsm.submission_date, gpl.title
from gse join gse_gsm using (gse) join gsm using (gsm) join gpl using (gpl)
where upper(gsm.characteristics_ch1) like '%AGE%' and
upper(gsm.characteristics_ch1) like '%TISSUE%' and
gsm.molecule_ch1 like '%total RNA%' and
gpl.technology='high-throughput sequencing' and
gpl.title like '%Illumina%' and
(gsm.organism_ch1='Homo sapiens' or
gsm.organism_ch1='Mus musculus' or
gsm.organism_ch1='Drosophila melanogaster' or
gsm.organism_ch1= 'Heterocephalus glaber' or
gsm.organism_ch1= 'Caenorhabditis elegans');"
"""
*/