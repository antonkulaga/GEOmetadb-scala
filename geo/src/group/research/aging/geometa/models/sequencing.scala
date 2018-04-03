package group.research.aging.geometa.models

import group.research.aging.geometa.Tables
import group.research.aging.util.NotNull
import io.circe.generic.JsonCodec
import shapeless.record._
import shapeless.syntax.singleton._
import shapeless.{LabelledGeneric, _}


@JsonCodec case class Sequencing_GSM(
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
                                    ) {

  lazy val asRecord = Sequencing_GSM.labeledGen.to(this)
  def asMap = asRecord.toMap
  def keys = asRecord.keys
  def fieldNames = keys.toList.map(_.toString.replace("'", ""))

  lazy val asGen = Sequencing_GSM.gen.to(this)
  def asList = asGen.toList
  def asStringList = asList.map(_.toString)
}


object Sequencing_GSM {

  val labeledGen =  LabelledGeneric[Sequencing_GSM]

  val gen = Generic[Sequencing_GSM]

  def fromGSM(gsm: Tables.gsm, technology: String): Sequencing_GSM = {
    val record  = Tables.gsm.labeledGen.to(gsm)
    val recSmall = (record - 'source_name_ch2 -'organism_ch2 -
      'characteristics_ch2 - 'molecule_ch2 - 'label_ch2 -
      'treatment_protocol_ch2 - 'extract_protocol_ch2 -'label_protocol_ch2) -
      'hyb_protocol - 'label_protocol_ch1 - 'label_ch1 - 'supplementary_file
    val recNew = recSmall + ('sequencer ->> technology)
    val r = labeledGen.from(recNew)
    gen.from(gen.to(r).map(NotNull))
  }


  def asMap(seq_gsm: Sequencing_GSM) = labeledGen.to(seq_gsm).toMap

}