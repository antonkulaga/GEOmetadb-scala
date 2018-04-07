import $ivy.`io.getquill::quill:2.3.3`
import $ivy.`com.typesafe.akka::akka-http:10.0.11`
import $ivy.`org.xerial:sqlite-jdbc:3.18.0`
import $ivy.`org.postgresql:postgresql:9.4.1208`
import $ivy.`io.getquill::quill-jdbc:2.3.3`
import $ivy.`com.lihaoyi::pprint:0.5.3`
import $ivy.`com.chuusai::shapeless:2.3.3`
import $ivy.`io.circe::circe-core:0.9.3`
import $ivy.`io.circe::circe-generic:0.9.3`
import $ivy.`io.circe::circe-generic-extras:0.9.3`
import $ivy.`io.circe::circe-parser:0.9.3`

import io.getquill.Embedded
import shapeless._
import record._
import ops.record._
import syntax.singleton._
import io.circe.generic.JsonCodec
import shapeless.record._
import shapeless.syntax.singleton._
import shapeless.{LabelledGeneric, _}


import shapeless.Poly1

object NotNull extends Poly1 {

  implicit val intCase: Case.Aux[Int, Int] =
    at[Int](v => v)

  implicit val longCase: Case.Aux[Long, Long] =
    at[Long](v => v)


  implicit val doubleCase: Case.Aux[Double, Double] =
    at[Double](v => if(v == Double.NaN) 0.0 else v)

  implicit val stringCase: Case.Aux[String, String] =
    at[String](v => if(v==null || v == "null") "" else v)

}


trait StringId[T] {//extends HasId[T, String] {
  def ID: String
  //lazy val id: model.persistence.Id[String] = model.persistence.Id(ID)
}

object Tables {


  case class gse
  (
    ID: Double, //ID	ID	real	database automatically assigned internal ID
    title: String, //Title	title	text	unique name describing the overall study
    gse: String, //GSE Acc(Link)	gse*	text	unique accession number approved and issued by GEO, NCBI
    status: String, //Data Status	status	text	date released to public
    submission_date: String, //Submission Date	submission_date	text	date submitted
    last_update_date: String, //Update Date	last_update_date	text	date last updated
    pubmed_id: Int, //Pubmed ID	pubmed_id	integer	NCBI PubMed identifier (PMID)
    summary: String, //Summary	summary	text	a description of the goals and objectives of this study
    `type`: String, //GSE Type	type	text	keyword(s)generally describing the type of study, e.g., time course, dose response, comparative genomic hybridization, ChIP-chip, cell type comparison, disease state analysis, stress response, genetic modification, etc.
    contributor: String, //Contributor	contributor	text	people contributed to this study
    contact: String, //Contact	contact	text	contact information for this study
    web_link: String, //Web link	web_link	text	a Web link to study and/or supplementary information about the study
    overall_design: String, //Overall Design	overall_design	text	overall design, a description of the experimental design, including information about how many samples are in the study, any control and/or reference samples, dye-swaps, etc
    repeats: String, //Repeats	repeats	text	repeat type, which can be biological replicate, technical replicate - extract, or technical replicate - labeled-extract
    repeats_sample_list: String, //Repeat Samples	repeats_sample_list	text	sample list in a repeat
    variable: String, //Variable	variable	text	variable type, e.g. dose, time, tissue, strain, gender, cell line, development stage, age, agent, cell type, infection, isolate, metabolism, shock, stress, temperature, specimen, disease state, protocol, growth protocol, genotype/variation, species, individual, or other. For example:
    variable_description: String, //Variable Description	variable_description	text	description of a variable type
    supplementary_file: String, //Supplementary	supplementary_file	text	ftp link to NCBI GEO supplementary file(s) of this GSE
    /*
    SOFT FTP†	 	 	ftp link to NCBI GEO SOFT format of this GSE
    SeriesMatrix FTP†	 	 	ftp link to NCBI GEO SOFT format of the Series Matrix
    GPL Acc	 	 	GPLs separated by comma
    GPL Count	 	 	number of GPLs
    GSM Acc	 	 	GSMs separated by comma
    GSM Count	 	 	number of GSMs
    */
  ) //extends DoubleId[gse]

  case class gpl(
                  ID: Double,
                  title: String,
                  gpl: String,
                  status: String,
                  submission_date: String,
                  last_update_date: String,
                  technology: String,
                  distribution: String,
                  organism: String,
                  manufacturer: String,
                  manufacture_protocol: String,
                  coating: String,
                  catalog_number: String,
                  support: String,
                  description: String,
                  web_link: String,
                  contact: String,
                  data_row_count: Double,
                  supplementary_file: String,
                  bioc_package: String
                ) //extends DoubleId[gpl]


  trait Ch2 {
    def source_name_ch2: String
    def organism_ch2: String
    def characteristics_ch2: String
    def molecule_ch2: String
    def label_ch2: String
    def treatment_protocol_ch2: String
    def extract_protocol_ch2: String
    def label_protocol_ch2: String
  }


  object gsm {
    val labeledGen = LabelledGeneric[Tables.gsm]
  }

  case class gsm(
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
                  label_ch1: String,
                  treatment_protocol_ch1: String,
                  extract_protocol_ch1: String,
                  label_protocol_ch1: String,
                  source_name_ch2: String,
                  organism_ch2: String,
                  characteristics_ch2: String,
                  molecule_ch2: String,
                  label_ch2: String,
                  treatment_protocol_ch2: String,
                  extract_protocol_ch2: String,
                  label_protocol_ch2: String,
                  hyb_protocol: String,
                  description: String,
                  data_processing: String,
                  contact: String,
                  supplementary_file: String,
                  data_row_count: Double,
                  channel_count: Double,
                ) extends Ch2 with StringId[gsm]


  case class gds(
                  ID: String,
                  gds: String,
                  title: String,
                  description: String,
                  `type`: String,
                  pubmed_id: String,
                  gpl: String,
                  platform_organism: String,
                  platform_technology_type: String,
                  feature_count: Int,
                  sample_organism: String,
                  sample_type: String,
                  channel_count: String,
                  sample_count: Int,
                  value_type: String,
                  gse: String,
                  order: String,
                  update_date: String
                ) extends StringId[gds]

  case class gse_gsm(gse: String, gsm: String)
  case class gse_gpl(gse: String, gpl: String)

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
                                      //label_ch1: String,
                                      treatment_protocol_ch1: String,
                                      extract_protocol_ch1: String,
                                      //label_protocol_ch1: String,
                                      //hyb_protocol: String,
                                      description: String,
                                      data_processing: String,
                                      contact: String,
                                      //supplementary_file: String,
                                      data_row_count: Double,
                                      channel_count: Double,
                                      sequencer: String
                                    ) extends StringId[Sequencing]{

  lazy val asRecord = Sequencing.labeledGen.to(this)
  def asMap = asRecord.toMap
  def keys = asRecord.keys
  def fieldNames = keys.toList.map(_.toString.replace("'", ""))

  lazy val asGen = Sequencing.gen.to(this)
  def asList = asGen.toList
  def asStringList = asList.map(_.toString)
}


object Sequencing {

  val labeledGen =  LabelledGeneric[Sequencing]

  val gen = Generic[Sequencing]

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


  def asMap(seq_gsm: Sequencing) = labeledGen.to(seq_gsm).toMap

}