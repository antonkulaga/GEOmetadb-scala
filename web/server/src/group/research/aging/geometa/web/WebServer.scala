package group.research.aging.geometa.web

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, ContentDispositionTypes}
import akka.http.scaladsl.server.directives.CachingDirectives
import akka.http.scaladsl.server.{HttpApp, RequestContext}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import group.research.aging.geometa.sequencing.{Converter, GEOmetaSequencing}
import group.research.aging.geometa.web.controller.SequencingController
import group.research.aging.util.PercentDecoder._
import kantan.csv.{CsvConfiguration, rfc}
import scalacss.DevDefaults._
import wvlet.log.LogFormatter.SourceCodeLogFormatter
import wvlet.log.{LogLevel, LogSupport, Logger}

import scala.xml.{Elem, Unparsed}
//import wvlet.log.{LogLevel, LogSupport, Logger}
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import cats.effect.IO
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import akka.http.scaladsl.model.headers._

import doobie.hikari._

// Server definition
object WebServer extends HttpApp with FailFastCirceSupport with LogSupport with CachingDirectives {

  //lazy val config = ConfigFactory.load().getString("sqlite")

  def sqliteUrl(str: String) = s"jdbc:sqlite:${str}"
   lazy val url = sqliteUrl("/pipelines/data/GEOmetadb.sqlite")

  implicit val postgresTransactor: IO[HikariTransactor[IO]] = HikariTransactor.newHikariTransactor[IO](
    "org.postgresql.Driver", url = "jdbc:postgresql://127.0.0.1:5432/sequencing", "postgres", "changeme"
  )

  val controller = new SequencingController(postgresTransactor)


  implicit val cacheSystem = ActorSystem("cacheSystem")


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

  implicit val tsvConfig: CsvConfiguration = rfc.withCellSeparator('\t').withHeader(true)


  def un(str: String): Unparsed = scala.xml.Unparsed(str)

  /**
    * Rendsers HTML page and calls AJAX method to load the data
    * @param page
    * @param parameters
    * @return
    */
  def loadPage(page: String, parameters: String*): Elem =
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
    complete(loadPage(page.decode))
  }

  //lazy val defaultLimit = 50

  def data = cache(routeCache, simpleKeyer){
    pathPrefix("data" / "sequencing") { complete{
      //controller.sequencing(limit = defaultLimit).asJson
      controller.loadSequencing(actions.QueryParameters.mus)
      }
    } ~ pathPrefix("data" / "gse" / Remaining) { series => complete{
      //controller.sequencing(limit = defaultLimit).asJson
      val gse = series.split('-').filter(_!="").toList
      controller.loadSequencing(actions.QueryParameters.empty.copy(series = gse))
      }
    } ~ pathPrefix("tsv" / "sequencing") { complete{
      //controller.sequencing(limit = defaultLimit).asJson
      //controller.loadSequencing(actions.QueryParameters.mus)
      List("")
    }
    } ~ pathPrefix("tsv" / "gse" / Remaining) { series => complete{
      //controller.sequencing(limit = defaultLimit).asJson
      val gse = series.split('-').filter(_!="").toList
      val loaded = controller.loadSequencing(actions.QueryParameters.empty.copy(series = gse))
      val str = loaded.sequencing.map(l=>l.asStringList.mkString("\t"))
      str
    }
    } ~ pathPrefix("temp" / Remaining) { series => complete{
      //controller.sequencing(limit = defaultLimit).asJson
      val gse = series.split('-').filter(_!="").toList
      val loaded = controller.loadSequencing(actions.QueryParameters.rna.copy(series = gse))
      val header = "GSM\tGSE\tSpecies\tSequencer\tType\tSex\tAge\tTissue\tExtracted molecule\tStrain\tComments"
      val result = header::loaded.sequencing.map(l=>l.gsm + "\t" + l.series_id + "\t" + l.organism_ch1 + "\t" + l.sequencer + "\tNA\tNA\tNA\tNA\tNA\tNA\tNA")   
      HttpResponse(  entity = HttpEntity(MediaTypes.`text/csv`.withCharset(HttpCharsets.`UTF-8`),  result.foldLeft(""){ case (acc, el)=> acc + el + "\n"}   ))
    }
    }
  }

  /*

  def mau_tau = cache(routeCache, simpleKeyer){
    pathPrefix("view" / "mus") { complete{
      controller.loadSequencing(actions.QueryParameters.mus)
      }
    } ~ pathPrefix("view" / "tau") { complete{
      controller.loadSequencing(actions.QueryParameters.taurus) }
    }
    }

  def download = cache(routeCache, simpleKeyer){pathPrefix("downloads"){
      pathPrefix("species" / Remaining ){ species =>

        val tsv = controller.loadSequencing(actions.QueryParameters.test).asCsv(this.tsvConfig)
        complete(
          HttpEntity(ContentTypes.`text/csv(UTF-8)`,tsv)
        )
      }
    }
  }
  */

  def suggest = cache(routeCache, simpleKeyer){
    pathPrefix("suggest"){
      path("species") {
        complete { controller.allOrganisms().asJson }
      } ~ path("platforms"){
        complete { controller.allSequencers().asJson }
      } ~ path("molecules"){
        complete { controller.allMolecules().asJson }
      }
    }
  }
  /*
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
      complete(loadPage(page.decode))
    } ~ data ~ suggest  ~ //download  ~
      path("public" / Segment){ name =>
        getFromResource(name.toString)
      }

}


