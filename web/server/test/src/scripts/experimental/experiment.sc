import $exec.classes
import $exec.dependencies
import cats.effect.IO
import cats.implicits._
import doobie._
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.meta.Meta
import doobie.util.transactor
import pprint.PPrinter.BlackWhite
import doobie.hikari._
import group.research.aging.geometa.{GEOmeta, QueryRunner}
import group.research.aging.geometa.sequencing._

def sqliteUrl(str: String) = s"jdbc:sqlite:${str}"
val sqliteConnectionURL = sqliteUrl("/pipelines/data/GEOmetadb.sqlite")
val postgresConnectionURL = "jdbc:postgresql://127.0.0.1:5432/postgres" //sequencing


implicit val mstr = Meta[String]

class ConverterExperimental(original: GEOmeta, transactor: IO[HikariTransactor[IO]])  extends Converter(original, transactor)
{
  override def insertFieldQuery(table: String, column: String, values: List[String]) = {
    val sql = s"INSERT INTO ${table} (${column}) values (?)"
    Update[String](sql).updateManyWithGeneratedKeys[String](column)(values)
  }
}
val transactor: IO[HikariTransactor[IO]] = HikariTransactor.newHikariTransactor[IO](
   "org.postgresql.Driver", url = postgresConnectionURL, "postgres", "changeme"
)

val controller = new GEOmeta(HikariTransactor.newHikariTransactor[IO]("org.sqlite.JDBC", sqliteConnectionURL, "", ""))
val converter: Converter = new ConverterExperimental(controller, transactor)

//controller.run(insertModels(List("one", "two", "three", "four")).compile.drain)
//BlackWhite.pprintln(converter.original.all_molecules())
//println("===")
//BlackWhite.pprintln(converter.original.all_species())

//println(converter.cleanSequencers())
println("===cleaning===")
BlackWhite.pprintln(converter.cleanSequencers())
BlackWhite.pprintln(converter.cleanMolecules())
BlackWhite.pprintln(converter.cleanOrganisms())

println("===sequencers===")
BlackWhite.pprintln(converter.allSequencers())
BlackWhite.pprintln(converter.addSequencers())
BlackWhite.pprintln(converter.allSequencers())
println("===organisms===")
BlackWhite.pprintln(converter.run(converter.countQuery("organisms")))
BlackWhite.pprintln(converter.addOrganisms())
BlackWhite.pprintln(converter.run(converter.countQuery("organisms")))
println("===molecules===")

BlackWhite.pprintln(converter.run(converter.countQuery("molecules")))
BlackWhite.pprintln(converter.addMolecules())
BlackWhite.pprintln(converter.run(converter.countQuery("molecules")))
println("----DONE--------")
