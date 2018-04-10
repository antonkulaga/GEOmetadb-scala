import $exec.geometa
import $exec.meta
import geometa._
import meta._
import java.io.File
import com.typesafe.config.ConfigFactory
//import group.research.aging.geometa.models._
import io.getquill.context.jdbc.JdbcContext
import io.getquill._
import scala.collection.immutable.SortedSet

import scala.collection.immutable._
//import wvlet.log.{LogLevel, LogSupport, Logger}

lazy val context: SqliteJdbcContext[Literal.type] = new SqliteJdbcContext(Literal, config)
val geo = new GEOmeta(context)

val species = "Heterocephalus glaber"
val v1 = geo.sequencing_by_species(species, 100, 0)
println("---")
println(v1.length)
println("==============")
//val v2 = geo.sequencing_by_species(species)
//println(v2.length)
val offset = 10
import context._
val q = context.quote{
  for {
    sample <- query[Tables.gsm]
    gpl <- query[Tables.gpl]
    if sample.gpl == gpl.gpl
    if sample.organism_ch1 == lift(species)
    if gpl.technology == lift(geo.technology)
  } yield { (sample, gpl.title) }
}
//val results = if(limit > 0) context.run(q.drop(lift(offset)).take(lift(limit))) else context.run(q.drop(lift(offset)))

//val r2 = context.run(q)
//println(r2.length)

//geo.Sequencing(50).size
//for(e <- geo.Sequencing(50)) println(Sequencing.asMap(e))
//println("==============")
//for(e <- geo.Sequencing(250)) println(e.characteristics_ch1)
//geo.count_molecule_aged().foreach(println)
//geo.count_molecule_aged_tissue().foreach(println)
//geo.all_field().foreach(println)
println("///////////////")

//Sqlite.columns("gpl")(ctx).foreach(println)
//Sqlite.columns("gse_gpl")(ctx).foreach(println)
//Sqlite.columns("gse_gsm")(ctx).foreach(println)
//geo.Sequencing(30).foreach(println(_))//(pprint.pprintln(_))
//geo.all_gpls().foreach(println)
//geo.all_sequencers().foreach(println)
//geo.all_species().foreach(println)