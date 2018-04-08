package group.research.aging.geometa

import io.getquill.{Literal, SqliteJdbcContext}
import shapeless._
import io.getquill._
import io.getquill.context.jdbc.JdbcContext

import scala.collection.immutable._
import group.research.aging.geometa.models._
//import wvlet.log.{LogLevel, LogSupport, Logger}

class GEOmeta(val context: JdbcContext[SqliteDialect, Literal.type])
{
  import context._

  lazy val technology: String = "high-throughput sequencing"

  /**
  sql="select gse.gse,gse.title, gse.overall_design,gsm.gsm, gsm.title,gsm.organism_ch1,gsm.characteristics_ch1,gsm.molecule_ch1,gsm.submission_date, gpl.title
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
    */

/*
  def limited[T](q: context.Quoted[context.Query[T]], limit: Int = 0, offset: Int = 0) = {
    if(limit>=0) q.drop(lift(offset)).take(lift(limit))  else  q.drop(lift(offset))
  }
*/

  def sequencing_by_species(species: String, limit: Int = 0, offset: Int = 0)= {
    val q = context.quote{
      for {
        sample <- query[Tables.gsm]
        gpl <- query[Tables.gpl]
        if sample.gpl == gpl.gpl
        if sample.organism_ch1 == lift(species)
        if gpl.technology == lift(technology)
      } yield { (sample, gpl.title) }
    }
    val results = if(limit > 0) context.run(q.drop(lift(offset)).take(lift(limit))) else context.run(q)
    results.map{ case (sample, title) => Sequencing.fromGSM(sample, get_sequencer(title))}.sortBy(g=>g.molecule_ch1)
  }

  def neededSequencing(limit: Int = 0, offset: Int = 0)= {
    val q = context.quote{
      for {
        sample <- query[Tables.gsm]
        gpl <- query[Tables.gpl]
        if sample.gpl == gpl.gpl
        if gpl.technology == lift(technology)
        if sample.characteristics_ch1 like "%age"
        if sample.characteristics_ch1 like "%tissue%"
        if sample.organism_ch1 like "Mus musculus"
        if sample.molecule_ch1 like "total RNA"
      } yield { (sample, gpl.title) }
    }
    val results = if(limit > 0) context.run(q.drop(lift(offset)).take(lift(limit))) else context.run(q)
    results.map{ case (sample, title) => Sequencing.fromGSM(sample, get_sequencer(title))}
  }

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

  def all_sequencers() = {
    val q = context.quote{
      query[Tables.gpl].filter(g=>g.technology == lift(technology)).map(_.title.toLowerCase).distinct
    }
    val platforms = context.run(q).map(get_sequencer)
    SortedSet(platforms:_*)
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

  /*
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
  */


  def gpl(limit: Int = 0, offset: Int = 0): List[Tables.gpl] = {
    val q = context.quote{
      query[Tables.gpl].filter(g=>g.technology == lift(technology))
    }
    if(limit > 0) context.run(q.drop(lift(offset)).take(lift(limit))) else context.run(q)
  }

}