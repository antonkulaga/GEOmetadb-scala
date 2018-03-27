package group.research.aging.geometa

import io.getquill.{Literal, SqliteJdbcContext}

import io.getquill.Embedded
import shapeless._
import record._
import ops.record._
import syntax.singleton._

case class Sequencing_GSM(
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
                           hyb_protocol: String,
                           description: String,
                           data_processing: String,
                           contact: String,
                           supplementary_file: String,
                           data_row_count: Double,
                           channel_count: Double,
                           sequencer: String = ""
                         )



object Sequencing_GSM {


  val fromGen =  LabelledGeneric[Tables.gsm]
  val toGen =  LabelledGeneric[Sequencing_GSM]

  def fromGSM(gsm: Tables.gsm, technology: String): Sequencing_GSM = {
    val recSmall = fromGen.to(gsm) - 'ch2
    //val recTec = rec + ('sequencer ->> technology)
    toGen.from(recSmall + ('sequencer ->> technology))
  }

}