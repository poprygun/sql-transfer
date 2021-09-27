# Spring Integration Sample

Spring integration flow is using MS Sql Server stored procedure as a source, selecting data from `chachkies_source` table, and loading data to a Postgres DB - `chachkies_destination` table.

Database initialization is performed by Spring Boot `schema.sql`.

Initial data is seeded by `data.sql`.


## Start DB container for source db - MS Sql Server

```bash
docker run --name 'sql1' -e 'ACCEPT_EULA=Y' -e 'MSSQL_SA_PASSWORD=Str0ngPa$$w0rd' -d -p 1433:1433 mcr.microsoft.com/mssql/server:2019-GDR1-ubuntu-16.04
```

## Start DB container for destination db - Postgres

```bash
docker run --name some-postgres -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -d postgres
```

## Trigger request by issuing get request to `http://localhost:8080/flow`

```bash
http :8080/flow
HTTP/1.1 200
Connection: keep-alive
Content-Type: application/json
Date: Mon, 27 Sep 2021 15:01:30 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "starFlow": "ACCEPTED"
}
```