package group.research.aging.geometa.sequencing


import doobie.{Fragment, Fragments}
import group.research.aging.geometa.core.QueryBuilder

import scala.collection.immutable.Nil
//import wvlet.log.{LogLevel, LogSupport, Logger}

import doobie.implicits._

import scala.collection.immutable.List

class QueryBuilderSequencing  extends QueryBuilder{

  def withSeries(values: List[String]): Option[doobie.Fragment] = addInOpt(fr"samples.gse", values)


  def notInExtraction(values: List[String]) = addInOpt(fr"extract_protocol", values)

  //def addMolecule(values: List[String]): Option[doobie.Fragment] = addInOpt(fr"sample.molecule_ch1", values)


  def characteristics_and(values: List[String], upper: Boolean = true) = {
    likesAdd(fr"samples.characteristics", values, upper)
  }

  def characteristics_or(values: List[String], upper: Boolean = true) = {
    likesOr(fr"samples.characteristics", values, upper)
  }


  /************PLACEHOLDER *******************************/

  def addMolecule(values: List[String]): Option[doobie.Fragment] = None //addInOpt(fr"gsm.molecule_ch1", values)

  def addSequencer(values: List[String]): Option[doobie.Fragment] = None //addInOpt(fr"gpl.title", values)
  //def likeAndSequencer(values: List[String]): Option[doobie.Fragment] = likesAdd(fr"gpl.title", values)
  def likeOrSequencer(values: List[String]): Option[doobie.Fragment] = None  //likesOr(fr"gpl.title", values)

  def withSpecies(values: List[String]): Option[doobie.Fragment] = None //addInOpt(fr"gsm.organism_ch1", values)

  val sampleSelection = sql"""
        SELECT samples.id, samples.title, samples.gsm, samples.gse, samples.gpl,
          sequencers.model, molecules.molecule, organisms.name,
          samples.characteristics, samples.status, samples.submission_date,
          samples.last_update_date, samples.type, samples.source_name,
          samples.treatment_protocol, samples.extract_protocol,
          samples.description, samples.data_processing, samples.contact
        FROM samples, organisms, molecules, sequencers
    """

  lazy val mainCondition =
    fr"""samples.organism_id = organisms.id AND samples.molecule_id = molecules.id AND samples.sequencer_id =  sequencers.id """


  def makeWhere(
                           species: List[String] = Nil,
                           molecules: List[String] = Nil,
                           sequencers: List[String] = Nil,
                           andLikeCharacteristics: List[String] = Nil,
                           orLikeCharacteristics: List[String] = Nil,
                           series: List[String] = Nil,
                           limit: Int = 0,
                           offset: Int = 0
                         ): Fragment =
    Fragments.whereAndOpt(
      withSpecies(species),
      addMolecule(molecules),
      likeOrSequencer(sequencers),
      characteristics_and(andLikeCharacteristics),
      characteristics_or(orLikeCharacteristics),
      withSeries(series)
    )

}

/**
  *
  *
CREATE TABLE "public"."molecules" (
    "id" integer DEFAULT nextval('molecules_id_seq') NOT NULL,
    "molecule" text NOT NULL
) WITH (oids = false);

CREATE TABLE "public"."organisms" (
    "id" integer DEFAULT nextval('organisms_id_seq') NOT NULL,
    "name" text NOT NULL
) WITH (oids = false);


DROP TABLE IF EXISTS "samples";
DROP SEQUENCE IF EXISTS samples_id_seq;
CREATE SEQUENCE samples_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1;

CREATE TABLE "public"."samples" (
    "id"  integer DEFAULT nextval('samples_id_seq') NOT NULL,
    "title" text NOT NULL,
    "gsm" text NOT NULL,
    "gse" text NOT NULL,
    "gpl" text NOT NULL,
    "sequencer_id" integer NOT NULL,
    "molecule_id" integer NOT NULL,
    "organism_id" integer NOT NULL,
    "characteristics" text NOT NULL,
    "status" text NOT NULL,
    "submission_date" text NOT NULL,
    "last_update_date" text NOT NULL,
    "type" text NOT NULL,
    "source_name" text NOT NULL,
    "treatment_protocol" text NOT NULL,
    "extract_protocol" text NOT NULL,
    "description" text NOT NULL,
    "data_processing" text NOT NULL,
    "contact" text NOT NULL,
    CONSTRAINT "samples_ID" PRIMARY KEY ("id")
) WITH (oids = false);

/* Sequencers table */

DROP TABLE IF EXISTS "sequencers";
DROP SEQUENCE IF EXISTS sequencers_id_seq;
CREATE SEQUENCE sequencers_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1;
CREATE TABLE "public"."sequencers" (
    "id" integer DEFAULT nextval('sequencers_id_seq') NOT NULL,
    "model" text NOT NULL
) WITH (oids = false);
  */
