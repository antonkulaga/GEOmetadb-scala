package group.research.aging.geometa.web

import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.server.{HttpApp, RequestContext}
import akka.http.scaladsl.server.directives.CachingDirectives
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import group.research.aging.geometa.GEOmeta
import group.research.aging.geometa.web.controller.Controller
import group.research.aging.utils.SimpleSourceFormatter
import io.getquill.{Literal, SqliteJdbcContext}
import scalacss.DevDefaults._
import io.circe.syntax._
import wvlet.log.LogFormatter.SourceCodeLogFormatter
import wvlet.log.{LogLevel, LogSupport, Logger}

// Server definition
object WebServer extends HttpApp with FailFastCirceSupport with LogSupport with CachingDirectives {

  // Set the default log formatter
  Logger.setDefaultFormatter(SourceCodeLogFormatter)
  Logger.setDefaultLogLevel(LogLevel.DEBUG)

  val simpleKeyer: PartialFunction[RequestContext, Uri] = {
    val isGet: RequestContext ⇒ Boolean = _.request.method == HttpMethods.GET
    val isAuthorized: RequestContext ⇒ Boolean = _.request.headers.exists(_.is(Authorization.lowercaseName))
    val fun: PartialFunction[RequestContext, Uri]= {
      case r: RequestContext if isGet(r) && !isAuthorized(r) ⇒ r.request.uri
    }
    fun
  }


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
        console.log({un("\""+ page+"\"")})
        MainJS.page({
        un("\""+ page+"\"")}{un(parameters.foldLeft(""){ (acc, el) => acc + ", " + "\""+ el+ "\""})
        })
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

  lazy val defaultLimit = 50

  def view = pathPrefix("view" / "sequencing") { complete{
      Controller.loadSequencing(defaultLimit).asJson
    }
  }
 /*

  def bySpeciesTSV = pathPrefix("downloads" / "species" / Remaining ) { species =>
    import kantan.csv._         // All kantan.csv types.
    import kantan.csv.ops._     // Enriches types with useful methods.

    Controller.bySpecies(species)
  }

 
  def species = cache(pathPrefix("view" / "gsm"), simpleKeyer) {
    complete {
      Controller.getAllSpecies()
      ???
    }
  }

  def species = cache(routeCache, simpleKeyer) {
    complete {
      Controller.getAllSpecies().toList.asJson
    }
  }
  */

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


