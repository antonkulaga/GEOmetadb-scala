package group.research.aging.geometa.sequencing
import cats.effect.IO
import cats.implicits._
import doobie._
import doobie.hikari.HikariTransactor
import doobie.implicits._
import group.research.aging.geometa.{GEOmeta, QueryRunner}

class Converter(val original: GEOmeta, implicit val transactor: IO[HikariTransactor[IO]])  extends QueryRunner
{

  //def clean(table: String) = run(cleanQuery(table).run)

  def cleanSequencers(): Int = run(cleanQuery("sequencers"))

  def cleanSamples() = run(cleanQuery("samples"))

  def cleanMolecules() = run(cleanQuery("molecules"))

  def cleanOrganisms() = run(cleanQuery("organisms"))

  def insertFieldQuery(table: String, column: String, values: List[String]) = {
    val sql = s"INSERT INTO ${table} (${column}) values (?)"
    Update[String](sql).updateManyWithGeneratedKeys[String](column)(values)
  }

  protected def insertSequencersQuery(models: List[String]): fs2.Stream[ConnectionIO, String] = {
    insertFieldQuery("sequencers", "model", models)
  }

  protected def insertMoleculesQuery(molecules: List[String]): fs2.Stream[ConnectionIO, String] = {
    insertFieldQuery("sequencers", "molecule", molecules)
  }

  protected def insertOrganismsQuery(organisms: List[String]): fs2.Stream[ConnectionIO, String] = {
    insertFieldQuery("organisms", "organism", organisms)
  }

  def addSequencers(): Seq[String] = {
    run(insertSequencersQuery(original.all_sequencers().toList).compile.toList)
  }

  def addMolecules(): Seq[String] = {
    run(insertMoleculesQuery(original.all_molecules()).compile.toList)
  }

  def addOrganisms(): Seq[String] = {
    run(insertOrganismsQuery(original.all_species()).compile.toList)
  }

  def allSequencers(): List[(Int, String)] = {
    run(sql"""SELECT sequencers.id, sequencers.model FROM sequencers""".query[(Int, String)].to[List])
  }

  def allOrganisms(): List[(Int, String)] = {
    run(sql"""SELECT organisms.id, organisms.organism FROM organisms""".query[(Int, String)].to[List])
  }

  def allMolecules(): List[(Int, String)] = {
    run(sql"""SELECT molecules.id, molecules.molecule FROM molecules""".query[(Int, String)].to[List])
  }


}
