CREATE DATABASE rabbitsave;

\c rabbitsave;

CREATE SEQUENCE sortsaveline_id_seq;

CREATE TABLE public.sortsaveline (	
  id int4 NOT NULL DEFAULT nextval('sortsaveline_id_seq') PRIMARY KEY,
	marketid int4 NOT NULL,
	matchid int4 NOT NULL,
	outcomeid varchar(255) NULL,
	receivedat int8 NOT NULL,
	savedat int8 NOT NULL,
	specifiers varchar(255) NULL
);

ALTER SEQUENCE sortsaveline_id_seq OWNED BY public.sortsaveline.id;
