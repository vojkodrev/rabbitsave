CREATE DATABASE rabbitsave;

\c rabbitsave;

CREATE TABLE public.sortsaveline (	
	marketid int4 NOT NULL,
	matchid int4 NOT NULL,
	outcomeid varchar(255) NULL,
	receivedat int8 NOT NULL,
	savedat int8 NOT NULL,
	specifiers varchar(255) NULL
);
