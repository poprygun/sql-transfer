IF NOT EXISTS(SELECT * FROM sys.databases WHERE name = 'test_db') CREATE DATABASE test_db;

USE test_db;

IF NOT EXISTS(SELECT SCHEMA_ID FROM sys.schemas WHERE name = 'test_schema') EXEC ('CREATE SCHEMA test_schema');

DROP TABLE IF EXISTS test_schema.chachkies_source;

create table test_schema.chachkies_source
(
    id varchar(36) not null,
    latitude decimal (9,6),
    longitude decimal(9,6)
)

DROP TABLE IF EXISTS test_schema.chachkies_destination;

create table test_schema.chachkies_destination
(
    id varchar(36) not null,
    latitude decimal (9,6),
    longitude decimal(9,6)
)

if object_id ( 'chachkiesproc', 'p' ) is not null drop procedure chachkiesproc;

create procedure chachkiesproc
(@latitude decimal(9,6))
as
begin
    set nocount on
    select id, latitude, longitude
    from test_schema.chachkies_source
    where latitude > @latitude
end
