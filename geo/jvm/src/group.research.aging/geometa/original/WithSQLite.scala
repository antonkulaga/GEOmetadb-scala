package group.research.aging.geometa.original

import scala.collection.immutable.List

import doobie.implicits._

trait WithSQLite {
  self : BasicGEO =>

  def liteColsQuery(value: String): doobie.Query0[(String, String)] = sql"""SELECT name, sql FROM sqlite_master WHERE tbl_name = $value AND type = 'table'""".query[(String, String)]
  def liteTableCols(value: String): List[(String, String)] = run(liteColsQuery(value).to[List])
}
