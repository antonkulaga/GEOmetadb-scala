package group.research.aging.geometa.core

import cats.effect.IO
import doobie.hikari.HikariTransactor
//import wvlet.log.{LogLevel, LogSupport, Logger}

import cats.implicits._
import doobie._
import doobie.implicits._

import scala.collection.immutable.List


trait QueryRunner {
  def transactor: IO[HikariTransactor[IO]]

  def run[T](q: doobie.ConnectionIO[T]): T =
    (for{ xa <- transactor ; selection <- q.transact(xa)} yield selection).unsafeRunSync

  def debug[T](q: Query0[T])=
    {
      transactor.flatMap{ xa =>
        val y = xa.yolo
        import y._
        q.check
      }
    }.unsafeRunSync()

  def countQuery(table: String) = (fr"select count(*) from" ++ Fragment.const(table)).query[Int].unique

  def cleanQuery(table: String): ConnectionIO[Int] = (fr"DELETE FROM sequencers" ++ Fragment.const(table)).update.run

  def cleanIncrement(sequence: String): ConnectionIO[Int] = (fr"ALTER SEQUENCE"++Fragment.const(sequence)++fr"RESTART WITH 1").update.run

  def insertFieldQuery(table: String, column: String, values: List[String]): fs2.Stream[ConnectionIO, String] = {
    val sql = s"INSERT INTO ${table} (${column}) values (?)"
    Update[String](sql).updateManyWithGeneratedKeys[String](column)(values)
  }

  //  protected def cleanQuery(table: String): Update0 = sql"DELETE FROM ${table}".update
}
