
-- Neue Tabelle prefixes
-- primary key "id"
CREATE TABLE prefixes
(
  id serial NOT NULL,
  shortname character varying(32),
  url_prefix character varying(1024),
  CONSTRAINT prefixes_pkey PRIMARY KEY (id),
  CONSTRAINT shortname_unique_idx UNIQUE (shortname)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE prefixes
  OWNER TO postgres;

-- Ein default-Eintrag, wird als Referenz für bereits existierende
-- Entries in Tabelle tickets verwendet.
INSERT INTO prefixes (id, shortname, url_prefix) VALUES ( 1, 'localhost:3000', 'http://localhost:3000/');

-- Neue Spalte prefixId in Tabelle tickets, sie referenziert
-- genau einen Eintrag in Tabelle prefixes.
ALTER TABLE tickets
   ADD COLUMN "prefixId" integer NOT NULL DEFAULT 1;

-- Foreign-Key constraint für Tabelle tickets
ALTER TABLE tickets
  ADD CONSTRAINT "prefixId_fk" FOREIGN KEY ("prefixId") REFERENCES prefixes (id)
   ON UPDATE RESTRICT ON DELETE RESTRICT;
CREATE INDEX "fki_prefixId_fk"
  ON tickets("prefixId");
