package group.research.aging.geometa.models

import group.research.aging.geometa.Tables
import group.research.aging.util.NotNull
import group.research.aging.util.NotNull.at
import io.circe.generic.JsonCodec
import shapeless.record._
import shapeless.syntax.singleton._
import shapeless._
import shapeless.Poly1


object SequencingGEO {

  val labeledGen =  LabelledGeneric[SequencingGEO]

  val gen = Generic[SequencingGEO]

  def asMap(seq_gsm: SequencingGEO) = labeledGen.to(seq_gsm).toMap

}


@JsonCodec case class SequencingGEO(
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
                                ){

  lazy val asRecord = SequencingGEO.labeledGen.to(this)
  def asMap = asRecord.toMap
  def keys = asRecord.keys
  def fieldNames = keys.toList.map(_.toString.replace("'", ""))

  lazy val asGen = SequencingGEO.gen.to(this)
  def asList = asGen.toList
  def asStringList = asList.map(_.toString)

  protected def get_sequencer(n: String): String =  n.indexOf(" (") match {
    case -1 => n.toLowerCase
    case v => n.substring(0, v).toLowerCase
  }

  def withFixedSequencer: SequencingGEO = this.copy(sequencer = get_sequencer(this.sequencer))
}
