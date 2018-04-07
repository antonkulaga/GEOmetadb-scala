package group.research.aging.geometa.models

import group.research.aging.geometa.{StringId, Tables}
import group.research.aging.util.NotNull
import group.research.aging.util.NotNull.at
import io.circe.generic.JsonCodec
import shapeless.record._
import shapeless.syntax.singleton._
import shapeless.{LabelledGeneric, _}
import model.persistence.HasId
import shapeless.Poly1

@JsonCodec case class Sequencing(
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