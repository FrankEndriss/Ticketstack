
-- Table: tickets

-- DROP TABLE tickets;

CREATE TABLE tickets
(
  ticket character varying NOT NULL, -- Ticket ID
  text character varying, -- Freitext
  prio integer, -- Sortierung
  tstate character varying(32),
  CONSTRAINT ticket_pk_idx PRIMARY KEY (ticket)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tickets
  OWNER TO postgres;
COMMENT ON COLUMN tickets.ticket IS 'Ticket ID';
COMMENT ON COLUMN tickets.text IS 'Freitext';
COMMENT ON COLUMN tickets.prio IS 'Sortierung';


-- Index: tickets_prio_idx

-- DROP INDEX tickets_prio_idx;

CREATE UNIQUE INDEX tickets_prio_idx
  ON tickets
  USING btree
  (prio);


