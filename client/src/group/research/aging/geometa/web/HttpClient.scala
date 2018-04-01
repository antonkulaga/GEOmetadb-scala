package group.research.aging.geometa.web
import cats.effect.IO
import io.circe.generic.auto._
import hammock._
import hammock.marshalling._
import hammock.js.Interpreter
import hammock.circe.implicits._

import scala.concurrent.Future

object HttpClient {
  implicit val interpreter = Interpreter[Future]

  def getLoaded() = Hammock.getWithOpts()

  val response = Hammock
    .request(Method.GET, uri"https://api.fidesmo.com/apps", Map()) // In the `request` method, you describe your HTTP request
    .as[List[String]]
    .exec[IO]
}