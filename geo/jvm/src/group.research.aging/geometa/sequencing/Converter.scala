package group.research.aging.geometa.sequencing
import cats.effect.IO
import cats.implicits._
import doobie._
import doobie.hikari.HikariTransactor
import doobie.implicits._
import group.research.aging.geometa.original.GEOmeta

class Converter(val original: GEOmeta, val sequencing: SequencingLoader)
{

  /*
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
   */

  protected lazy val insertIntoSamplesUpdate: doobie.Update0 =
    sql"""
         INSERT INTO samples (
          title, gsm, gse, gpl, sequencer_id, molecule_id, organism_id,
          characteristics, status, submission_date, last_update_date, type, source_name,
          treatment_protocol, extract_protocol, description, data_processing, contact
          )
         (SELECT DISTINCT
          COALESCE(gsm.title, ''), gsm.gsm, gsm.series_id, gsm.gpl, sequencers.id, molecules.id, organisms.id,
          COALESCE(gsm.characteristics_ch1, ''), COALESCE(gsm.status, ''), COALESCE(gsm.submission_date, ''),
          COALESCE(gsm.last_update_date, ''), COALESCE(gsm.type, ''), COALESCE(gsm.source_name_ch1,''),
          COALESCE(gsm.treatment_protocol_ch1, ''), COALESCE(gsm.extract_protocol_ch1, ''), COALESCE(gsm.description, ''),
          COALESCE(gsm.data_processing, ''), COALESCE(gsm.contact, '')
         FROM gsm, gpl, sequencers, molecules, organisms, TRIM(SUBSTRING(LOWER(gpl.title), 0, POSITION('(' IN gpl.title))) as sequencer
         WHERE gsm.gpl = gpl.gpl AND
          gpl.technology = 'high-throughput sequencing' AND
          sequencers.model = sequencer AND
          molecules.molecule = gsm.molecule_ch1 AND
          organisms.name = gsm.organism_ch1
          );
       """.update

  lazy val checkSamplesQuery =
    sql"""
          SELECT gsm.title AS title, gsm.gsm, gsm.series_id AS gse, gsm.gpl,
                 sequencers.id AS sequencer_id, molecules.id as molecule_id, organisms.id AS organism_id,
                 gsm.characteristics_ch1 AS characteristics,
                 gsm.status, gsm.submission_date, gsm.last_update_date, gsm.type, gsm.source_name_ch1 AS source,
                 gsm.treatment_protocol_ch1 AS treatment_protocol, gsm.extract_protocol_ch1 AS extract_protocol,
                 gsm.description, gsm.data_processing, gsm.contact
               FROM gsm, gpl, sequencers, molecules, organisms, TRIM(SUBSTRING(LOWER(gpl.title), 0, POSITION('(' IN gpl.title))) as sequencer
               WHERE gsm.gpl = gpl.gpl AND
                 gpl.technology = 'high-throughput sequencing' AND
                 sequencers.model = sequencer AND
                 molecules.molecule = gsm.molecule_ch1 AND
                 organisms.name = gsm.organism_ch1
               LIMIT 30
      """.query[(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String)]

  protected def insertSequencersQuery(models: List[String]): fs2.Stream[ConnectionIO, String] = {
    sequencing.insertFieldQuery("sequencers", "model", models)
  }

  protected def insertMoleculesQuery(molecules: List[String]): fs2.Stream[ConnectionIO, String] = {
    sequencing.insertFieldQuery("molecules", "molecule", molecules)
  }

  protected def insertOrganismsQuery(organisms: List[String]): fs2.Stream[ConnectionIO, String] = {
    sequencing.insertFieldQuery("organisms", "name", organisms)
  }

  def addSequencers() = {
    sequencing.run(insertSequencersQuery(original.all_sequencers().toList).compile.toList)
  }

  def addMolecules() = {
    sequencing.run(insertMoleculesQuery(original.all_molecules().toList).compile.toList)
  }

  def addOrganisms() = {
    sequencing.run(insertOrganismsQuery(original.all_species().toList).compile.toList)
  }


  def addSamples(): Int = {
    sequencing.run(insertIntoSamplesUpdate.run)
  }


}
