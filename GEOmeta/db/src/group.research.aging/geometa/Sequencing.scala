package group.research.aging.geometa

import io.getquill.{Literal, SqliteJdbcContext}

import io.getquill.Embedded
import shapeless._
import record._
import ops.record._
import syntax.singleton._
import io.circe.generic.JsonCodec
import io.circe.generic.extras._

import shapeless.{Poly1, _}
import shapeless.ops.hlist._
import shapeless.ops.record._

object NotNull extends Poly1 {

  implicit val intCase: Case.Aux[Int, Int] =
    at[Int](v => v)

  implicit val longCase: Case.Aux[Long, Long] =
    at[Long](v => v)


  implicit val doubleCase: Case.Aux[Double, Double] =
    at[Double](v => if(v == Double.NaN) 0.0 else v)

  implicit val stringCase: Case.Aux[String, String] =
    at[String](v => if(v==null || v == "null") "" else v)

}