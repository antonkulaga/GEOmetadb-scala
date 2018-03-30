package group.research.aging.geometa

import io.getquill.Embedded
import model.persistence.HasId
import shapeless._
import record._
import syntax.singleton._
import record._
import syntax.singleton._

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
    )

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
                  )

  case class Ch2(source_name_ch2: String,
                 organism_ch2: String,
                 characteristics_ch2: String,
                 molecule_ch2: String,
                 label_ch2: String,
                 treatment_protocol_ch2: String,
                 extract_protocol_ch2: String,
                 label_protocol_ch2: String) extends Embedded

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
                  ch2: Ch2,
                  hyb_protocol: String,
                  description: String,
                  data_processing: String,
                  contact: String,
                  supplementary_file: String,
                  data_row_count: Double,
                  channel_count: Double,
                )


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
                )

  case class gse_gsm(gse: String, gsm: String)
  case class gse_gpl(gse: String, gpl: String)



}
