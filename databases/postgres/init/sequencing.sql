-- Adminer 4.6.2 PostgreSQL dump

DROP TABLE IF EXISTS "molecules";
DROP SEQUENCE IF EXISTS molecules_id_seq;
CREATE SEQUENCE molecules_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START  CACHE 1;

CREATE TABLE "public"."molecules" (
    "id" integer DEFAULT nextval('molecules_id_seq') NOT NULL,
    "molecule" text NOT NULL
) WITH (oids = false);


DROP TABLE IF EXISTS "organisms";
DROP SEQUENCE IF EXISTS organisms_id_seq;
CREATE SEQUENCE organisms_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 2186 CACHE 1;

CREATE TABLE "public"."organisms" (
    "id" integer DEFAULT nextval('organisms_id_seq') NOT NULL,
    "organism" text NOT NULL
) WITH (oids = false);


DROP TABLE IF EXISTS "samples";
CREATE TABLE "public"."samples" (
    "id" real NOT NULL,
    "title" text NOT NULL,
    "gsm" text NOT NULL,
    "gse" text NOT NULL,
    "sequencer" text NOT NULL,
    "status" text NOT NULL,
    "submission_date" text NOT NULL,
    "last_update_date" text NOT NULL,
    "type" text NOT NULL,
    "source_name" text NOT NULL,
    "organism" text NOT NULL,
    "characteristics" text NOT NULL,
    "molecule" text NOT NULL,
    "treatment_protocol" text NOT NULL,
    "extract_protocol" text NOT NULL,
    "description" text NOT NULL,
    "data_processing" text NOT NULL,
    "contact" text NOT NULL,
    "gpl" text NOT NULL,
    CONSTRAINT "samples_ID" PRIMARY KEY ("id")
) WITH (oids = false);

CREATE INDEX "samples_gsm" ON "public"."samples" USING btree ("gsm");

CREATE INDEX "samples_molecule" ON "public"."samples" USING btree ("molecule");

CREATE INDEX "samples_organism" ON "public"."samples" USING btree ("organism");

CREATE INDEX "samples_series_id" ON "public"."samples" USING btree ("gse");


DROP TABLE IF EXISTS "sequencers";
DROP SEQUENCE IF EXISTS sequencer_id_seq;
CREATE SEQUENCE sequencer_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 45 CACHE 1;

CREATE TABLE "public"."sequencers" (
    "id" integer DEFAULT nextval('sequencer_id_seq') NOT NULL,
    "model" text NOT NULL
) WITH (oids = false);


-- 2018-06-16 19:32:30.361854+00