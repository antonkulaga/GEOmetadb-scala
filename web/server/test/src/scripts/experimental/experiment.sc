import $exec.dependencies
import dependencies._
import $exec.classes
import classes._
import classes.GEOmeta


import doobie._
import doobie.implicits._
import cats._
import cats.data._
import cats.effect.IO
import cats.implicits._
import pprint.PPrinter.BlackWhite
import doobie.hikari._
import doobie.hikari.implicits._
import cats.Reducible._
import shapeless.record._
import doobie.util.meta._
import doobie.util.meta.Meta

def sqliteUrl(str: String) = s"jdbc:sqlite:${str}"
val url = sqliteUrl("/pipelines/data/GEOmetadb.sqlite")
implicit val mstr = Meta[String]



implicit val transactor: IO[HikariTransactor[IO]] = HikariTransactor.newHikariTransactor[IO](
    "org.sqlite.JDBC", url, "", ""
  )

val controller = new Controller(transactor)

//def cols(value: String)= sql"""SELECT name, sql FROM sqlite_master WHERE tbl_name = $value AND type = 'table'""".query[(String, String)]


//val q = sql"""SELECT name, sql FROM sqlite_master WHERE tbl_name = 'gsm' AND type = 'table'""".query[(String, String)]
//controller.debug(controller.loadSequencingQuery(QueryParameters.test))
//BlackWhite.pprintln(controller.run(controller.loadSequencingQuery(QueryParameters.test).to[List]))
//BlackWhite.pprintln(controller.all_molecules())
//println("====================================")
//BlackWhite.pprintln(controller.all_species())
//println("====================================")
BlackWhite.pprintln(controller.all_sequencers())
println("====================================")
/*
//controller.debug(q)
//BlackWhite.pprintln(controller.run(q.to[List]))
//BlackWhite.pprintln(controller.run(cols("gsm").to[List]))
*/