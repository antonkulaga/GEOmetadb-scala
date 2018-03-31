package group.research.aging.geometa.web

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