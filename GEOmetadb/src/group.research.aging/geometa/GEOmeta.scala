package group.research.aging.geometa

import io.getquill.{Literal, SqliteJdbcContext}

import shapeless._
import io.getquill._
import io.getquill.context.jdbc.JdbcContext
import scala.collection.immutable._

class GEOmeta(context: JdbcContext[SqliteDialect, Literal.type]) {
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


  def sequencing_gsm(limit: Int)= {
    val q = context.quote{
      val results = for {
        sample <- query[Tables.gsm]
        gpl <- query[Tables.gpl]
        if sample.gpl == gpl.gpl
        if gpl.technology == lift(technology)
      } yield { (sample, gpl.title) }
      results.take(lift(limit))
    }
    context.run(q).map{ case (sample, title) => Sequencing_GSM.fromGSM(sample, get_sequencer(title))}
  }


  def gsm(limit: Int): List[Tables.gsm] = {
    val q = context.quote{
      query[Tables.gsm].take(lift(limit))
    }
    context.run(q)
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
  
  def gpl(limit: Int): List[Tables.gpl] = {
    val q = context.quote{
      query[Tables.gpl].filter(g=>g.technology == lift(technology)).take(lift(limit))
    }
    context.run(q)
  }

}