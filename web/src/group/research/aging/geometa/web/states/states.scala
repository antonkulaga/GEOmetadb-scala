package group.research.aging.geometa.web.states

import cats.kernel.Monoid
import group.research.aging.geometa.web.actions
import io.circe.generic._

import scala.collection.immutable._


object Table{
  lazy val empty = Table(Nil, Nil)
}
@JsonCodec case class Table(headers: List[String], data: List[List[String]])

object State{

  lazy val empty = State("none", actions.LoadedSequencing.empty, Table.empty)

  implicit def monoid: cats.Monoid[State] = new Monoid[State] {
    override def empty: State = State.empty

    override def combine(x: State, y: State): State = y //ugly TODO: rewrite
  }
}

@JsonCodec case class State (page: String,
                             sequencing: actions.LoadedSequencing,
                             table: Table = Table.empty,
                             errors: List[actions.ExplainedError] = Nil)


/*
// Data type for the entire application state:
trait State {
  def page: String
  def headers: List[String]
  def data: List[List[String]]
}

case object DefaultState extends State{
  lazy val page = "index"
  val headers = Nil
  val data = Nil
}

case object TestState extends State{
  lazy val page = "test"
  val headers = List("one", "two", "three")
  val data = List(
    List("one_value1", "two_value1", "three_value1"),
    List("one_value2", "two_value2", "three_value2"),
    List("one_value3", "two_value3", "three_value3"),
  )
}
*/

