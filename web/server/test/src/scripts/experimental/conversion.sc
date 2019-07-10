import $exec.classes
import $exec.dependencies
import cats.effect.IO
import cats.implicits._
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie._
import doobie.hikari.HikariTransactor
//import doobie.util.meta.Meta
import group.research.aging.geometa.original.GEOmeta
import group.research.aging.geometa.sequencing._
import pprint.PPrinter.BlackWhite
import doobie.implicits._
import cats.effect.IOApp
import cats.effect.ContextShift
import scala.concurrent.ExecutionContext.global
import doobie.util.ExecutionContexts

// We need a ContextShift[IO] before we can construct a Transactor[IO]. The passed ExecutionContext
// is where nonblocking operations will be executed. For testing here we're using a synchronous EC.
implicit val cs = IO.contextShift(ExecutionContexts.synchronous)


def sqliteUrl(str: String) = s"jdbc:sqlite:${str}"
val sqliteConnectionURL = sqliteUrl("/data/databases/sqlite/GEOmetadb.sqlite")

implicit val mstr = Meta[String]
val p = ExecutionContexts.fixedThreadPool[IO](32)


implicit val sqliteTransactor: Transactor.Aux[IO, Unit] =  Transactor.fromDriverManager[IO](
  "org.sqlite.JDBC", sqliteConnectionURL, "", ""
)

val geo = new GEOmeta(sqliteTransactor)

//val host = "db" //127.0.0.1
//val jdbcUrl = s"jdbc:postgresql://${host}:5432/sra"

val postgresConnectionURL = "jdbc:postgresql://127.0.0.1:5432/sra" //sequencing

val username = "postgres"
val password = "changeme"
/*
val poolSize = 40
val config = new HikariConfig()
config.setJdbcUrl(jdbcUrl)
config.setUsername(username)
config.setPassword(password)
config.setMaximumPoolSize(poolSize)
*/
/*
implicit val postgresTransactor: IO[HikariTransactor[IO]] =
  IO.pure(HikariTransactor.apply[IO](new HikariDataSource(config)))
*/

val postgresTransactor: Transactor.Aux[IO, Unit]  = Transactor.fromDriverManager[IO]("org.postgresql.Driver", postgresConnectionURL, username, password)

val controller = new SequencingLoader(postgresTransactor)

class ConverterExperimental(original: GEOmeta, loader: SequencingLoader)  extends Converter(original, loader)
{
  /*
  def insertFieldQuery(table: String, column: String, values: List[String]) = {
    val sql = s"INSERT INTO ${table} (${column}) values (?)"
    Update[String](sql).updateManyWithGeneratedKeys[String](column)(values)
  }
  */
}
/*
val transactor: IO[HikariTransactor[IO]] = HikariTransactor.newHikariTransactor[IO](
   "org.postgresql.Driver", url = postgresConnectionURL, "postgres", "changeme"
)
*/

val converter: Converter = new ConverterExperimental(geo, controller)

println("===cleaning===")
BlackWhite.pprintln(converter.sequencing.cleanSequencers())
BlackWhite.pprintln(converter.sequencing.cleanMolecules())
BlackWhite.pprintln(converter.sequencing.cleanOrganisms())
BlackWhite.pprintln(converter.sequencing.cleanSamples())


println("===sequencers===")
BlackWhite.pprintln(converter.sequencing.run(converter.sequencing.countQuery("sequencers")))
BlackWhite.pprintln(converter.addSequencers())
BlackWhite.pprintln(converter.sequencing.run(converter.sequencing.countQuery("sequencers")))

println("===organisms===")
BlackWhite.pprintln(converter.sequencing.run(converter.sequencing.countQuery("organisms")))
BlackWhite.pprintln(converter.addOrganisms())
BlackWhite.pprintln(converter.sequencing.run(converter.sequencing.countQuery("organisms")))


println("===molecules===")

BlackWhite.pprintln(converter.sequencing.run(converter.sequencing.countQuery("molecules")))
BlackWhite.pprintln(converter.addMolecules())
BlackWhite.pprintln(converter.sequencing.run(converter.sequencing.countQuery("molecules")))

println("===samples===")

BlackWhite.pprintln(converter.sequencing.run(converter.sequencing.countQuery("samples")))
converter.addSamples()
BlackWhite.pprintln(converter.sequencing.run(converter.sequencing.countQuery("samples")))

println("----DONE--------")
