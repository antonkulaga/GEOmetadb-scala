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
SELECT samples.id, samples.title, samples.gsm, samples.gse, samples.gpl,
          sequencers.model, molecules.molecule, organisms.name,
          samples.characteristics, samples.status, samples.submission_date,
          samples.last_update_date, samples.type, samples.source_name,
          samples.treatment_protocol, samples.extract_protocol,
          samples.description, samples.data_processing, samples.contact
  FROM samples, organisms, molecules, sequencers
  WHERE samples.organism_id = organisms.id AND samples.molecule_id = molecules.id AND samples.sequencer_id =  sequencers.id
LIMIT 30
  */


@JsonCodec case class Sequencing(
                                     id: String,
                                     title: String,
                                     gsm: String,
                                     gse: String,
                                     gpl: String,
                                     sequencer: String,
                                     molecule: String,
                                     organism: String,
                                     characteristics: String,
                                     status: String,
                                     submission_date: String,
                                     last_update_date: String,
                                     `type`: String,
                                     source_name: String,
                                     treatment_protocol: String,
                                     extract_protocol: String,
                                     description:  String,
                                     data_processing: String,
                                     contact: String,
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
