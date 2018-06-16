package group.research.aging.geometa

import cats.effect.IO
import doobie._
import doobie.hikari.HikariTransactor
import doobie.implicits._


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

  //  protected def cleanQuery(table: String): Update0 = sql"DELETE FROM ${table}".update
}
