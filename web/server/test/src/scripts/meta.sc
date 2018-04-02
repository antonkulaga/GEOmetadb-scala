import java.io.File

import $exec.geometa
import geometa._
import com.typesafe.config.ConfigValueFactory
import com.typesafe.config.{Config, ConfigFactory}
import io.getquill._
import io.getquill.context.jdbc.JdbcContext
import shapeless._


import scala.collection.immutable.SortedSet

val config = ConfigFactory.parseFile(new File("/pipelines/sources/GEOmetadb-scala/GEOmetadb/resources/application.conf"))
  .getConfig("quill-cache.sqlite")

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

  //Homo sapiens	Mus musculus	Rattus norvegicus	Drosophila melanogaster	Caenorhabditis elegans
  def count_molecule_aged() = {
    val q = context.quote{
      val results = for {
        sample <- query[Tables.gsm]
        gpl <- query[Tables.gpl]
        if sample.gpl == gpl.gpl
        if gpl.technology == lift(technology)
        if sample.organism_ch1 !=null
        if sample.characteristics_ch1.toLowerCase like "%age%"
        //gfif sample.characteristics_ch1.toLowerCase like "%tissue%"
        //if sample.molecule_ch1 !=null
      } yield (sample.organism_ch1, sample.molecule_ch1)
      results
    }
    val r = context.run(q)
    r.groupBy(r=> r).map{ case ((o, m), g) => (o, m, g.size) }.toList.sortBy(_._1)
  }

  def count_molecule() = {
    val q = context.quote{
      val results = for {
        sample <- query[Tables.gsm]
        gpl <- query[Tables.gpl]
        if sample.gpl == gpl.gpl
        if gpl.technology == lift(technology)
        if sample.organism_ch1 !=null
        //if sample.molecule_ch1 !=null
      } yield (sample.organism_ch1, sample.molecule_ch1)
      results
    }
    val r = context.run(q)
    r.groupBy(r=> r).map{ case ((o, m), g) => (o, m, g.size) }.toList.sortBy(_._1)
  }

  def all_field() = {
    val q = context.quote{
      val results = for {
        sample <- query[Tables.gsm]
        gpl <- query[Tables.gpl]
        if sample.gpl == gpl.gpl
        if gpl.technology == lift(technology)
        //if sample.supplementary_file != null
        if sample.treatment_protocol_ch1 != null
      } yield { sample.treatment_protocol_ch1 }
      results.distinct
    }
    val results = context.run(q)
    SortedSet(results:_*)
  }



  def gpl(limit: Int): List[Tables.gpl] = {
    val q = context.quote{
      query[Tables.gpl].filter(g=>g.technology == lift(technology)).take(lift(limit))
    }
    context.run(q)
  }

}

lazy val ctx: SqliteJdbcContext[Literal.type] = new SqliteJdbcContext(Literal, config)
import ctx._
val geo = new GEOmeta(ctx)


println("==============")
//for(e <- geo.sequencing_gsm(50)) println(Sequencing_GSM.asMap(e))
//println("==============")
//for(e <- geo.sequencing_gsm(250)) println(e.characteristics_ch1)
//geo.count_molecule_aged().foreach(println)
//geo.count_molecule_aged_tissue().foreach(println)
geo.all_field().foreach(println)
println("///////////////")

//Sqlite.columns("gpl")(ctx).foreach(println)
//Sqlite.columns("gse_gpl")(ctx).foreach(println)
//Sqlite.columns("gse_gsm")(ctx).foreach(println)
//geo.sequencing_gsm(30).foreach(println(_))//(pprint.pprintln(_))
//geo.all_gpls().foreach(println)
//geo.all_sequencers().foreach(println)
//geo.all_species().foreach(println)