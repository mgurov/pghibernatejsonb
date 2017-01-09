-- This DDL is used by flyway to initially setup the table
-- which will hold the log entries
--


-- Simple table for holding logs
create table LOGGING (
    date TIMESTAMP not null,
    data jsonb not null
);