CREATE TABLE resubs
(
   ticket character varying(256) NOT NULL REFERENCES tickets, 
   resubts timestamp without time zone NOT NULL, 
   text character varying(2048)
) 
WITH (
  OIDS = FALSE
)
;
ALTER TABLE public.resubs
  OWNER TO postgres;

