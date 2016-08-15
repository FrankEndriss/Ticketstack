-- definition table tickets, runnable on pgSQL
-- Table: tickets

-- DROP TABLE tickets;

CREATE TABLE tickets
(
  ticket character varying(256) NOT NULL,
  text character varying(2048),
  prio integer,
  tstate character(5) NOT NULL DEFAULT 'DO'::bpchar,
  CONSTRAINT tickets_pkey PRIMARY KEY (ticket),
  CONSTRAINT tstate_constraint CHECK (tstate = ANY (ARRAY['DO'::bpchar, 'WAIT'::bpchar, 'RESUB'::bpchar, 'DONE'::bpchar]))
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tickets
  OWNER TO postgres;



