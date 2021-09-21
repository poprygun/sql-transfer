use test_db

DROP TABLE IF EXISTS test_schema.chachkies;

create table test_schema.chachkies
(
    id varchar(36) not null,
    latitude decimal (9,6),
    longitude decimal(9,6)
);

if object_id ( 'chachkiesproc', 'p' ) is not null drop procedure chachkiesproc;

create procedure chachkiesproc
(@latitude decimal(9,6))
    as
begin
    set nocount on
    select id, latitude, longitude
    from test_schema.chachkies
    where latitude > @latitude
end;