package group.research.aging.geometa

import io.getquill.{Literal, SqliteJdbcContext}

import io.getquill.Embedded
import shapeless._
import record._
import ops.record._
import syntax.singleton._
import io.circe.generic.JsonCodec
import io.circe.generic.extras._

import shapeless.{Poly1, _}
import shapeless.ops.hlist._
import shapeless.ops.record._

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
                         )


object Sequencing_GSM {

  implicit val config: Configuration = Configuration.default.withDefaults

  val gen = Generic[Sequencing_GSM]
  val labeledGen =  LabelledGeneric[Sequencing_GSM]

  def fromGSM(gsm: Tables.gsm, technology: String): Sequencing_GSM = {
    val record  = Tables.gsm.labeledGen.to(gsm)
    val recSmall = (record - 'ch2) - 'hyb_protocol - 'label_protocol_ch1 - 'label_ch1 - 'supplementary_file
    val recNew = recSmall + ('sequencer ->> technology)
    val r = labeledGen.from(recNew)
    gen.from(gen.to(r).map(NotNull))
  }

  def asMap(seq_gsm: Sequencing_GSM) = labeledGen.to(seq_gsm).toMap


}