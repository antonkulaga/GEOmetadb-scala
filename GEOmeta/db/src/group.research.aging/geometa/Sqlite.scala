package group.research.aging.geometa

import io.getquill._

class Sqlite(context: SqliteJdbcContext[Literal.type]){
  import context._


  def tables() = {
    val query = quote(infix"""SELECT name FROM sqlite_master WHERE type='table'""".as[Query[String]])
    context.run(query)
  }

  def columns(tableName: String) = {
    import context._
    val query = quote{
      infix"SELECT sql FROM sqlite_master WHERE  tbl_name = ${lift(tableName)} AND type='table'".as[Query[String]]
    }
    context.run(query)
  }

}