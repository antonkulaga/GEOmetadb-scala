package group.research.aging.geometa.web

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.HttpApp
import akka.http.scaladsl.server.Route
import scalacss.DevDefaults._
import shared.Hello._
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._

// Server definition
object WebServer extends HttpApp {

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
        alert({"\""+ page+"\""})
        MainJS.page({"\""+ page+"\""}"{parameters.foldLeft(""){ (acc, el) => acc + ", " + "\""+ el+ "\""}})
      </script>
    </body>
    </html>


  lazy val resourcePrefix = "public"

  def mystyles = path("styles" / "mystyles.css"){
    complete  {
      HttpResponse(  entity = HttpEntity(MediaTypes.`text/css`.withCharset(HttpCharsets.`UTF-8`),  MyStyles.render   ))   }
  }

  def pages =  pathPrefix("pages" / Remaining) { file =>
    complete(loadPage("gsm"))
  }


  override def routes =
    (pathSingleSlash |  path("index.html")) {
      complete(loadPage("gsm"))
    } ~ mystyles ~
      pathPrefix("pages" / Remaining) { page =>
      complete(loadPage(page))
    } ~
      path("public" / Segment){ name =>
        getFromResource(name.toString)
      }

}


