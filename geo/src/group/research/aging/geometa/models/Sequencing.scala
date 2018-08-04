package group.research.aging.geometa.models


import group.research.aging.geometa.Tables
import group.research.aging.util.NotNull
import group.research.aging.util.NotNull.at
import io.circe.generic.JsonCodec
import shapeless.record._
import shapeless.syntax.singleton._
import shapeless._
import shapeless.Poly1


object Sequencing {

  val labeledGen =  LabelledGeneric[Sequencing]

  val gen = Generic[Sequencing]

  def asMap(seq_gsm: Sequencing) = labeledGen.to(seq_gsm).toMap

}

/**
SELECT samples.id, samples.title, samples.gsm, samples.gse, organisms.name,
           molecules.molecule, sequencers.model, samples.characteristics,
           samples.description, samples.treatment_protocol, samples.extract_protocol,
           samples.source_name, samples.data_processing,
           samples.submission_date, samples.last_update_date, samples.status,
           samples.type, samples.contact, samples.gpl
  FROM samples, organisms, molecules, sequencers
  WHERE samples.organism_id = organisms.id AND samples.molecule_id = molecules.id AND samples.sequencer_id =  sequencers.id
LIMIT 30
  */


@JsonCodec case class Sequencing(
                                     id: String,
                                     title: String,
                                     gsm: String,
                                     gse: String,
                                     organism: String,
                                     molecule: String,
                                     sequencer: String,
                                     characteristics: String,
                                     description:  String,
                                     treatment_protocol: String,
                                     extract_protocol: String,
                                     source_name: String,
                                     data_processing: String,
                                     submission_date: String,
                                     last_update_date: String,
                                     status: String,
                                     `type`: String,
                                     contact: String,
                                     gpl: String
                                ){

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
