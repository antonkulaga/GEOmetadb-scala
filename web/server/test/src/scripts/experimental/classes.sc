import $exec.dependencies
//import group.research.aging.geometa.models._

import scala.collection.immutable._

import cats.effect.IO
import cats.implicits._
import doobie._
import doobie.hikari._
import doobie.implicits._
import io.circe.generic.JsonCodec
import shapeless._
import shapeless.record._
import wvlet.log.LogSupport


println("starting")

object Sequencing {

  val labeledGen =  LabelledGeneric[Sequencing]

  val gen = Generic[Sequencing]

  /*
  def fromGSM(gsm: Tables.gsm, technology: String): Sequencing = {
    val record  = Tables.gsm.labeledGen.to(gsm)
    val recSmall = (record - 'source_name_ch2 -'organism_ch2 -
      'characteristics_ch2 - 'molecule_ch2 - 'label_ch2 -
      'treatment_protocol_ch2 - 'extract_protocol_ch2 -'label_protocol_ch2) -
      'hyb_protocol - 'label_protocol_ch1 - 'label_ch1 - 'supplementary_file
    val recNew = recSmall + ('sequencer ->> technology)
    val r = labeledGen.from(recNew)
    gen.from(gen.to(r).map(NotNull))
  }
  */


  def asMap(seq_gsm: Sequencing) = labeledGen.to(seq_gsm).toMap

}

case class Sequencing(
                                  ID: String,
                                  title: String,
                                  gsm: String,
                                  series_id: String,
                                  gpl: String,
                                  status: String,
                                  submission_date: String,
                                  last_update_date: String,
                                  `type`: String,
                                  source_name_ch1: String,
                                  organism_ch1: String,
                                  characteristics_ch1: String,
                                  molecule_ch1: String,
                                  treatment_protocol_ch1: Option[String],
                                  extract_protocol_ch1: Option[String],
                                  description:  Option[String],
                                  data_processing: Option[String],
                                  contact: String,
                                  data_row_count: Double,
                                  channel_count: Double,
                                  sequencer: String
                                ) {

  lazy val asRecord = Sequencing.labeledGen.to(this)
  def asMap = asRecord.toMap
  def keys = asRecord.keys
  def fieldNames = keys.toList.map(_.toString.replace("'", ""))

  lazy val asGen = Sequencing.gen.to(this)
  def asList = asGen.toList
  def asStringList = asList.map(_.toString)

  protected def get_sequencer(n: String): String =  n.indexOf(" (") match {
    case -1 => n.toLowerCase
    case v => n.substring(0, v).toLowerCase
  }

  def withFixedSequencer: Sequencing = this.copy(sequencer = get_sequencer(this.sequencer))
}



trait BasicGEO{

  def transactor: IO[HikariTransactor[IO]]

  def run[T](q: doobie.ConnectionIO[T]): T =
    (for{ xa <- transactor ; selection <- q.transact(xa)} yield selection).unsafeRunSync

  def debug[T](q: Query0[T])=
    {
      transactor.flatMap{ xa =>
        val y = xa.yolo
        import y._
        q.check
      }
    }.unsafeRunSync()

  lazy val technology: String = "high-throughput sequencing"
  lazy val sequencingTech = Fragments.and(fr"sample.gpl = gpl.gpl", fr"gpl.technology = ${technology}")

  protected def addOpt(fieldName: Fragment, value: Option[String]) = value.map(sp=> fr" UPPER(" ++ fieldName ++ fr") = ${sp.toUpperCase}")

  protected def addInOpt(fieldName: Fragment, values: List[String]): Option[doobie.Fragment] = if(values.size > 1)
  {
    val frag: Fragment = fr" UPPER(" ++ fieldName ++ fr")"
    values.toNel.map(v => Fragments.in(frag, v.map(_.toUpperCase)))
  } else addOpt(fieldName, values.headOption)

  protected def addSpecies(values: List[String]) = addInOpt(fr"sample.organism_ch1", values)
  protected def addMolecule(values: List[String]) = addInOpt(fr"sample.molecule_ch1", values)
  protected def addSequencer(values: List[String]) = addInOpt(fr"gpl.title", values)
  protected def likeAndSequencer(values: List[String]): Option[doobie.Fragment] = likesAdd(fr"gpl.title", values)
  protected def likeOrSequencer(values: List[String]): Option[doobie.Fragment] = likesOr(fr"gpl.title", values)


  protected def likeInside(fieldName: Fragment, values: List[String], upper: Boolean) = {
    val f = if(upper) fr"UPPER("++fieldName ++ fr")" else fieldName
    val cased = values.map(v => if(upper)  "%" + v.toUpperCase + "%" else "%" + v + "%")
    cased.map(v=> f ++ fr"LIKE ${v}")
  }

  protected def likesAdd(fieldName: Fragment, values: List[String], upper: Boolean = true): Option[doobie.Fragment] = if(values.isEmpty) None else {
    Some(fr"(" ++Fragments.and(likeInside(fieldName, values, upper):_*) ++ fr")")
  }

  protected def likesOr(fieldName: Fragment, values: List[String], upper: Boolean = true): Option[doobie.Fragment] = if(values.isEmpty) None else {
    Some(fr"(" ++ Fragments.or(likeInside(fieldName, values, upper):_*) ++ fr")")
  }

  protected def characteristics_and(values: List[String], upper: Boolean = true) = {
    likesAdd(fr"sample.characteristics_ch1", values, upper)
  }

  protected def characteristics_or(values: List[String], upper: Boolean = true) = {
    likesOr(fr"sample.characteristics_ch1", values, upper)
  }

  protected def limitation(limit: Int = 0, offset: Int = 0) = if(limit <= 0) fr"" else if(offset <= 0) fr"LIMIT $limit" else fr"LIMIT $limit OFFSET ${offset}"

  val sampleSelection =
    sql"""SELECT sample.ID, sample.title, sample.gsm, sample.series_id, sample.gpl, sample.status,
          sample.submission_date, sample.last_update_date, sample.type, sample.source_name_ch1,
          sample.organism_ch1, sample.characteristics_ch1, sample.molecule_ch1,
          sample.treatment_protocol_ch1, sample.extract_protocol_ch1,
          sample.description, sample.data_processing, sample.contact,
          sample.data_row_count, sample.channel_count, gpl.title
        FROM gsm sample, gpl """

  val sampleCount =
    sql"""SELECT count(*)
        FROM gsm sample, gpl """


  lazy val modelOrganisms = addSpecies(List("Homo sapiens",
    "Mus musculus","Drosophila melanogaster",
    "Heterocephalus glaber", "Caenorhabditis elegans"))

  def allBy(field: Fragment) = {
    sql"SELECT DISTINCT " ++ field ++ fr"FROM gsm sample, gpl " ++ Fragments.whereAndOpt(
      Some(sequencingTech)
    ) ++ fr"ORDER BY" ++ field
  }
}

import wvlet.log.LogSupport

import scala.collection.immutable._
//import wvlet.log.{LogLevel, LogSupport, Logger}

import cats.effect.IO
import cats.implicits._
import doobie._
import doobie.hikari._
import doobie.implicits._


class GEOmeta(val transactor: IO[HikariTransactor[IO]]) extends BasicGEO with LogSupport
{

  protected def makeWhere(species: List[String] = Nil,
                          molecules: List[String] = Nil,
                          sequencers: List[String] = Nil,
                          andLikeCharacteristics: List[String] = Nil,
                          orLikeCharacteristics: List[String] = Nil,
                          limit: Int = 0, offset: Int = 0
                         ): Fragment =
    Fragments.whereAndOpt( addSpecies(species),
      addMolecule(molecules), addSequencer(sequencers),
      characteristics_and(andLikeCharacteristics),  characteristics_or(orLikeCharacteristics))


  def sequencingQuery(species: List[String] = Nil,
                      molecules: List[String] = Nil,
                      sequencers: List[String] = Nil,
                      andLikeCharacteristics: List[String] = Nil,
                      orLikeCharacteristics: List[String] = Nil,
                      limit: Int = 0, offset: Int = 0) = {
    val where =  makeWhere(species, molecules, sequencers, andLikeCharacteristics, orLikeCharacteristics)
    (sampleSelection ++ where ++ limitation(limit, offset)).query[Sequencing]
  }

  def sequencing( species: List[String] = Nil,
                  molecules: List[String] = Nil,
                  sequencers: List[String] = Nil,
                  andLikeCharacteristics: List[String] = Nil,
                  orLikeCharacteristics: List[String] = Nil,
                  limit: Int = 0, offset: Int = 0) = {
    run( sequencingQuery(species, molecules, sequencers, andLikeCharacteristics, orLikeCharacteristics, limit, offset).to[List])
  }

  protected def get_sequencer(n: String): String =  n.indexOf(" (") match {
    case -1 => n.toLowerCase
    case v => n.substring(0, v).toLowerCase
  }

  def all_sequencers() = {
    val q: doobie.ConnectionIO[List[String]] =
      sql"""SELECT DISTINCT gpl.title
        FROM gpl
        WHERE gpl.technology = ${technology}
        ORDER BY gpl.title;""".query[String].to[List]
    val list = run(q).map(get_sequencer)
    SortedSet(list:_*)
  }


  def all_molecules() = run(allBy(fr"sample.molecule_ch1").query[String].to[List])
  def all_species() = run(allBy(fr"gpl.organism").query[String].to[List]).flatMap(s=>s.split("\t").map(_.trim)).distinct
}

object QueryParameters {
  lazy val empty = QueryParameters()

  lazy val test = QueryParameters(
    species = List("Mus musculus", "Bos taurus"),
    molecules = List("total RNA"),
    andLikeCharacteristics = List("age"),
    orLikeCharacteristics = List("kidney", "liver"),
    limit = 50
  )

}

case class SuggestionsInfo(
                            species: List[String],
                            molecules: List[String] = Nil,
                            sequencers: List[String] = Nil,
                          )

case class QueryParameters(
               species: List[String] = Nil,
               molecules: List[String] = Nil,
               sequencers: List[String] = Nil,
               andLikeCharacteristics: List[String] = Nil,
               orLikeCharacteristics: List[String] = Nil,
               limit: Int = 50,
               offset: Int = 0
             )

case class LoadedSequencing(
                             suggestionInfo: SuggestionsInfo,
                             queryParameters: QueryParameters,
                             sequencing: List[Sequencing]
                           )

class Controller(transactor: IO[HikariTransactor[IO]]) extends GEOmeta(transactor){


  def loadSequencingQuery( parameters: QueryParameters) = {
    super.sequencingQuery(species = parameters.species,
      molecules = parameters.molecules,
      sequencers = parameters.sequencers,
      andLikeCharacteristics = parameters.andLikeCharacteristics,
      orLikeCharacteristics = parameters.orLikeCharacteristics,
      limit = parameters.limit,
      offset = parameters.offset)
  }

  def loadSequencing(
                      parameters: QueryParameters
                    ) = {
    val gsms = super.sequencing(species = parameters.species,
      molecules = parameters.molecules,
      sequencers = parameters.sequencers,
      andLikeCharacteristics = parameters.andLikeCharacteristics,
      orLikeCharacteristics = parameters.orLikeCharacteristics,
      limit = parameters.limit,
      offset = parameters.offset)
    val suggestions = SuggestionsInfo(super.all_species().toList, super.all_sequencers().toList, super.all_molecules().toList) //TODO: fix collections
    LoadedSequencing(suggestions, parameters, gsms)
    //actions
  }

}

println("Classes Loaded")