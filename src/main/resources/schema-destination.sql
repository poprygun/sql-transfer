drop schema if exists test_schema cascade;

create schema test_schema;

create table test_schema.chachkies_destination
(
    id varchar(36) not null,
    latitude decimal (9,6),
    longitude decimal(9,6)
);
