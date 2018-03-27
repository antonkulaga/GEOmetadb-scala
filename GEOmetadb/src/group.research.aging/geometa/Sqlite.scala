package group.research.aging.geometa

import io.getquill._

object Sqlite{
  def tables(implicit context: SqliteJdbcContext[Literal.type]) = {
    import context._
    val query = quote(infix"""SELECT name FROM sqlite_master WHERE type='table'""".as[Query[String]])
    context.run(query)
  }

  def columns(tableName: String)(implicit context: SqliteJdbcContext[Literal.type]) = {
    import context._
    val query = quote{
      infix"SELECT sql FROM sqlite_master WHERE  tbl_name = '${lift(tableName)}' AND type='table'".as[Query[String]]
    }
    context.run(query)
  }

}