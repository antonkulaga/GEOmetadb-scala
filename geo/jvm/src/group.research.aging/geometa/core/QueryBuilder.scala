package group.research.aging.geometa.core
//import wvlet.log.{LogLevel, LogSupport, Logger}

import cats.implicits._
import doobie._
import doobie.implicits._

import scala.collection.immutable.List

trait QueryBuilder {

   def addOpt(fieldName: Fragment, value: Option[String]): Option[Fragment] = value.map(sp=> fr" UPPER(" ++ fieldName ++ fr") = ${sp.toUpperCase}")

   def addInOpt(fieldName: Fragment, values: List[String]): Option[doobie.Fragment] = if(values.size > 1)
  {
    val frag: Fragment = fr" UPPER(" ++ fieldName ++ fr")"
    values.toNel.map(v => Fragments.in(frag, v.map(_.toUpperCase)))
  } else addOpt(fieldName, values.headOption)

  def not(fragment: Option[Fragment]): Option[doobie.Fragment] = fragment.map(frag=> fr"NOT ("++ frag ++ fr")")

  
   def likeInside(fieldName: Fragment, values: List[String], upper: Boolean): List[Fragment] = {
    val f = if(upper) fr"UPPER("++fieldName ++ fr")" else fieldName
    val cased = values.map(v => if(upper)  "%" + v.toUpperCase + "%" else "%" + v + "%")
    cased.map(v=> f ++ fr"LIKE ${v}")
  }

   def likesAdd(fieldName: Fragment, values: List[String], upper: Boolean = true): Option[doobie.Fragment] = if(values.isEmpty) None else {
    Some(fr"(" ++Fragments.and(likeInside(fieldName, values, upper):_*) ++ fr")")
  }

   def likesOr(fieldName: Fragment, values: List[String], upper: Boolean = true): Option[doobie.Fragment] = if(values.isEmpty) None else {
    Some(fr"(" ++ Fragments.or(likeInside(fieldName, values, upper):_*) ++ fr")")
  }

   def limitation(limit: Int = 0, offset: Int = 0) = if(limit <= 0) fr"" else if(offset <= 0) fr"LIMIT $limit" else fr"LIMIT $limit OFFSET ${offset}"

}
