package group.research.aging.geometa

import io.getquill.{Literal, SqliteJdbcContext}

import io.getquill.Embedded
import shapeless._
import record._
import ops.record._
import syntax.singleton._



object GEOmeta extends GEOmeta
class GEOmeta {

  def gsm(implicit context: SqliteJdbcContext[Literal.type]) = {
    import context._

  }

}
