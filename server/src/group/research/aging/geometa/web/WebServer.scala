package group.research.aging.geometa.web

import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.HttpApp
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import group.research.aging.geometa.GEOmeta
import group.research.aging.geometa.web.controller.Controller
import io.getquill.{Literal, SqliteJdbcContext}
import scalacss.DevDefaults._
import io.circe.syntax._

// Server definition
object WebServer extends HttpApp with FailFastCirceSupport{

  def un(str: String) = scala.xml.Unparsed(str)

  def loadPage(page: String, parameters: String*) =
    <html>
      <head>
        <meta charset="utf-8" />
        <title>GEOmetadb UI</title>
        <script type="text/javascript" src="https://code.jquery.com/jquery-3.3.1.js"></script>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.3.1/semantic.css" />
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.3.1/semantic.js"></script>
        <link rel="stylesheet" href="/styles/mystyles.css" />
      </head>
    <body id="main">
      <script type="text/javascript" src="/public/out.js">
      </script>
      <script type="text/javascript">
        alert({un("\""+ page+"\"")})
        MainJS.page({un("\""+ page+"\"")}"{un(parameters.foldLeft(""){ (acc, el) => acc + ", " + "\""+ el+ "\""})})
      </script>
    </body>
    </html>


  lazy val resourcePrefix = "public"

  def mystyles = path("styles" / "mystyles.css"){
    complete  {
      HttpResponse(  entity = HttpEntity(MediaTypes.`text/css`.withCharset(HttpCharsets.`UTF-8`),  MyStyles.render   ))   }
  }

  def pages =  pathPrefix("pages" / Remaining) { page =>
    complete(loadPage(page))
  }

  def view = pathPrefix("view" / "gsm") {
    complete{
      Controller.loadSamplesPage().asJson
    }
  }

  override def routes =
    (pathSingleSlash |  path("index.html")) {
      complete(loadPage("sequencing"))
    } ~ mystyles ~
      pathPrefix("pages" / Remaining) { page =>
      complete(loadPage(page))
    } ~ view ~
      path("public" / Segment){ name =>
        getFromResource(name.toString)
      }

}


