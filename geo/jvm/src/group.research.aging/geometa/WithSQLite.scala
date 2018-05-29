package group.research.aging.geometa

import shapeless._

import scala.collection.immutable._
import group.research.aging.geometa.models._
import wvlet.log.LogSupport
//import wvlet.log.{LogLevel, LogSupport, Logger}

import doobie._
import doobie.implicits._
import cats.implicits._
import cats.effect.IO
import pprint.PPrinter.BlackWhite
import doobie.hikari._
import doobie.hikari.implicits._

trait WithSQLite {
  self : BasicGEO =>

  def liteColsQuery(value: String)= sql"""SELECT name, sql FROM sqlite_master WHERE tbl_name = $value AND type = 'table'""".query[(String, String)]
  def liteTableCols(value: String) = run(liteColsQuery(value).to[List])
}
