// build.sc
import mill._
import mill.define.Sources
import mill.scalajslib.ScalaJSModule
import mill.scalalib._
import scalajslib._
import ammonite.ops._
import coursier.maven.MavenRepository
import mill.scalalib.publish.{Developer, License, PomSettings, VersionControl}

val scala_version = "2.12.8"

val scala_js_version = "0.6.26"

val src = "src"

val resolvers =  Seq(
  MavenRepository("https://oss.sonatype.org/content/repositories/releases"),
  MavenRepository("https://bintray.com/hseeberger/maven"),
  MavenRepository("http://dl.bintray.com/micronautics/scala"),
  MavenRepository("https://dl.bintray.com/comp-bio-aging/main")
)

val plugins = Agg(ivy"org.scalamacros:::paradise:2.1.1")

val generalPomSettings =  PomSettings(
	description = "GEOmetadbLite",
	organization = "group.research.aging",
	url = "https://github.com/antonkulaga/GEOmetadb-scala",
	licenses = Seq(License.`MPL-2.0`),
	versionControl = VersionControl.github("antonkulaga", "GEOmetadb-scala"),
	developers = Seq(
		Developer("Kulaga", "Anton","https://github.com/antonkulaga")
	)
)

object geo extends Module {

  self =>

  def scalaVersion = scala_version

  def geo_ivy_deps = Agg(
    ivy"com.lihaoyi::pprint:0.5.4",
    ivy"io.circe::circe-core:0.11.1", //0.10.0
    ivy"io.circe::circe-generic:0.11.1", //0.10.0
    ivy"io.circe::circe-generic-extras:0.11.1", //0.10.0
    ivy"io.circe::circe-parser::0.11.1", //0.10.0
    ivy"org.wvlet.airframe::airframe-log::19.4.1"
	)

  object js extends ScalaJSModule {
    def scalaVersion = scala_version

    def scalaJSVersion = scala_js_version


    override def ivyDeps = geo_ivy_deps

    override def scalacPluginIvyDeps = super.scalacPluginIvyDeps() ++ plugins

		override def sources = T.sources(
			millSourcePath / src,
			millSourcePath / up / src
		)

  }

  object jvm extends ScalaModule with PublishModule {
    def scalaVersion = scala_version

    override def repositories = super.repositories ++ resolvers

		override def millSourcePath = super.millSourcePath / up

		override def sources = T.sources(
			millSourcePath / src,
			millSourcePath / "jvm" / src
		)

    override def ivyDeps = geo_ivy_deps ++ Agg(
  		ivy"org.xerial:sqlite-jdbc:3.27.2.1",
      //ivy"org.postgresql:postgresql:9.4.1208",
			ivy"org.postgresql:postgresql:42.2.5",
			ivy"com.kailuowang::henkan-convert:0.6.3",
      ivy"com.kailuowang::henkan-optional:0.6.3",
      ivy"org.tpolecat::doobie-core:0.6.0",
      ivy"org.tpolecat::doobie-postgres:0.6.0",
      ivy"org.tpolecat::doobie-hikari:0.6.0"
    )

    override def scalacPluginIvyDeps = plugins


		override def pomSettings =  generalPomSettings

		override def publishVersion = "0.0.7"
	}
}

object web extends Module{

	self=>

  lazy val ivyDeps =  Agg(
    ivy"com.pepegar::hammock-core::0.9.1",
    ivy"com.pepegar::hammock-circe::0.9.1",
    ivy"com.github.japgolly.scalacss::core::0.5.5",
    ivy"com.nrinaudo::kantan.csv-generic::0.5.0",
    ivy"com.nrinaudo::kantan.csv-cats::0.5.0",
 //   ivy"group.research.aging::cromwell-client::0.0.13",
    ivy"io.lemonlabs::scala-uri::1.4.5"
  )


	def postgres() = T.command{
		import ammonite.ops._
		val compose = pwd / 'databases / 'postgres
		%("docker", "stack", "deploy", "-c", "docker-compose.yml", "postgres")(compose)
	}


	object client extends ScalaJSModule {
	  def scalaVersion = scala_version

	  def scalaJSVersion = scala_js_version

	  //override def mainClass = Some("group.research.aging.geometa.web.MainJS")

	  override def moduleDeps = Seq(geo.js)

	  override def repositories = super.repositories ++ resolvers

	  override def ivyDeps = self.ivyDeps ++ Agg(
		ivy"org.scala-js::scalajs-dom::0.9.6",
        ivy"in.nvilla::monadic-html::0.4.0-RC1",
		ivy"org.querki::jquery-facade::1.2"
	  )

	 override def scalacPluginIvyDeps = super.scalacPluginIvyDeps() ++ plugins

		override def millSourcePath = super.millSourcePath / up

		override def sources = T.sources(
			millSourcePath / "client" / src,
      millSourcePath / src
		)

		//override def runClasspath = T {
		//	super.runClasspath().filterNot(_.path.last.contains("scala-xml"))
		//}
	
	}

	object server extends ScalaModule {

	  override def moduleDeps = Seq(geo.jvm)

	  def scalaVersion = scala_version


	  override def ivyDeps = self.ivyDeps ++ Agg(
			ivy"com.typesafe.akka::akka-stream:2.5.22",
			ivy"com.typesafe.akka::akka-http:10.1.8",
			ivy"com.typesafe.akka::akka-http-xml:10.1.8",
			ivy"com.typesafe.akka::akka-http-caching:10.1.8",
			ivy"de.heikoseeberger::akka-http-circe:1.25.2", //1.22.0",
			ivy"com.pepegar::hammock-akka-http:0.9.1",
			ivy"org.apache.jena:jena-arq:3.10.0"
		)

	  override def repositories = super.repositories ++ resolvers
	  
	  override def resources = T.sources {
			def base : Seq[Path] = super.resources().map(_.path)
			def jsout = client.fastOpt().path / up
			(base ++ Seq(jsout)).map(PathRef(_))
	  }	 

		override def sources = T.sources(
			millSourcePath / src,
			millSourcePath / up / src
		)

	 override def scalacPluginIvyDeps = plugins

	  object test extends Tests{

			override def ivyDeps = Agg(
				ivy"org.scalatest::scalatest:3.0.7"
			)

			def testFrameworks = Seq("org.scalatest.tools.Framework")
		}


	}
}
