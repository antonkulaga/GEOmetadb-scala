import $exec.geometa

import java.io.File
import com.typesafe.config.ConfigFactory
import geometa._

//import group.research.aging.geometa.models._
import io.getquill.context.jdbc.JdbcContext
import io.getquill._
import scala.collection.immutable.SortedSet

import scala.collection.immutable._
//import wvlet.log.{LogLevel, LogSupport, Logger}

val config = ConfigFactory.parseFile(new File("/pipelines/sources/GEOmetadb-scala/web/server/resources/application.conf")).getConfig("quill-cache.sqlite")

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
    val results = if(limit > 0) context.run(q.drop(lift(offset)).take(lift(limit))) else context.run(q.drop(lift(offset)))
    results.map{ case (sample, title) => Sequencing_GSM.fromGSM(sample, get_sequencer(title))}
  }

  def sequencing_gsm(limit: Int = 0, offset: Int = 0)= {
    val q = context.quote{
      for {
        sample <- query[Tables.gsm]
        gpl <- query[Tables.gpl]
        if sample.gpl == gpl.gpl
        if gpl.technology == lift(technology)
      } yield { (sample, gpl.title) }
    }
    println(q.ast)
    val results = if(limit > 0) context.run(q.drop(lift(offset)).take(lift(limit))) else context.run(q.drop(lift(offset)))
    results.map{ case (sample, title) => Sequencing_GSM.fromGSM(sample, get_sequencer(title))}
  }


  def gsm(limit: Int = 0, offset: Int = 0): List[Tables.gsm] = {
    val q = context.quote{
      query[Tables.gsm]
    }
    if(limit > 0) context.run(q.drop(lift(offset)).take(lift(limit))) else context.run(q.drop(lift(offset)))
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
    val q = context.quote{
      query[Tables.gpl].filter(g=>g.technology == lift(technology)).map(_.organism).distinct
    }
    context.run(q).toList
  }

  def gpl(limit: Int = 0, offset: Int = 0): List[Tables.gpl] = {
    val q = context.quote{
      query[Tables.gpl].filter(g=>g.technology == lift(technology))
    }
    if(limit > 0) context.run(q.drop(lift(offset)).take(lift(limit))) else context.run(q.drop(lift(offset)))
  }

}