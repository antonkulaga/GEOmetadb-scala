// build.sc
import mill._
import mill.define.Sources
import mill.scalajslib.ScalaJSModule
import mill.scalalib._
import scalajslib._
import ammonite.ops._
import coursier.maven.MavenRepository

val scala_version = "2.12.4"

val scala_js_version = "0.6.22"

val src = "src"

val resolvers =  Seq(
  MavenRepository("https://oss.sonatype.org/content/repositories/releases"),
  MavenRepository("https://bintray.com/hseeberger/maven"),
  MavenRepository("http://dl.bintray.com/micronautics/scala"),
  MavenRepository("https://dl.bintray.com/denigma/denigma-releases"),
  MavenRepository("https://dl.bintray.com/comp-bio-aging/main")
)

val plugins = Agg(ivy"org.scalamacros:::paradise:2.1.1")

object geo extends Module {

  self =>

  def scalaVersion = scala_version

  def geo_ivy_deps = Agg(
    ivy"com.lihaoyi::pprint:0.5.3",
    iv
    ivy"io.circe::circe-core:0.9.3",
    ivy"io.circe::circe-generic:0.9.3",
    ivy"io.circe::circe-generic-extras:0.9.3",
    ivy"io.circe::circe-parser:0.9.3",
		ivy"org.wvlet.airframe::airframe-log::0.41"
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

  object jvm extends ScalaModule{
    def scalaVersion = scala_version

    override def repositories = super.repositories ++ resolvers

		override def millSourcePath = super.millSourcePath / up

		override def sources = T.sources(
			millSourcePath / src,
			millSourcePath / "jvm" / src
		)

    override def ivyDeps = geo_ivy_deps ++ Agg(
      ivy"io.getquill::quill:2.3.3",
      ivy"org.xerial:sqlite-jdbc:3.18.0",
      ivy"org.postgresql:postgresql:9.4.1208",
      ivy"com.kailuowang::henkan-convert:0.6.1",
      ivy"com.kailuowang::henkan-optional:0.6.1",
      ivy"com.micronautics::quill-cache:3.5.9",
      ivy"io.getquill::quill-jdbc:2.3.3"
    )


    override def scalacPluginIvyDeps = plugins

  }
}

object web extends Module{

	self=>

  lazy val ivyDeps =  Agg(
    ivy"com.pepegar::hammock-core::0.8.3",
    ivy"com.pepegar::hammock-circe::0.8.3",
    ivy"com.github.japgolly.scalacss::core::0.5.5",
		ivy"com.nrinaudo::kantan.csv-generic::0.4.0",
		ivy"com.nrinaudo::kantan.csv-cats::0.4.0",
		ivy"group.research.aging::cromwell-client::0.0.13",
    ivy"io.lemonlabs::scala-uri::1.1.1"
  )

	object client extends ScalaJSModule {
	  def scalaVersion = scala_version

	  def scalaJSVersion = scala_js_version

	  //override def mainClass = Some("group.research.aging.geometa.web.MainJS")

	  override def moduleDeps = Seq(geo.js)

	  override def repositories = super.repositories ++ resolvers

	  override def ivyDeps = self.ivyDeps ++ Agg(
			ivy"org.scala-js::scalajs-dom::0.9.4",
      ivy"in.nvilla::monadic-html::0.4.0-RC1"
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
			ivy"io.getquill::quill:2.3.3",
			ivy"com.micronautics::quill-cache:3.5.9",
			ivy"com.typesafe.akka::akka-stream:2.5.11",
			ivy"com.typesafe.akka::akka-http:10.1.0",
			ivy"com.typesafe.akka::akka-http-xml:10.1.0",
			ivy"com.typesafe.akka::akka-http-caching:10.1.0",
			ivy"de.heikoseeberger::akka-http-circe:1.20.0",
			ivy"com.pepegar::hammock-akka-http:0.8.3"
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
		  ivy"io.getquill::quill:2.3.3",
		  ivy"org.scalatest::scalatest:3.0.5"
		)

		def testFrameworks = Seq("org.scalatest.tools.Framework")
	  }

	}
}
