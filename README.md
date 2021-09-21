# Spring Integration Sample

Using MS Sql Server stored procedure

## Start DB container

```bash
docker run --name 'sql1' -e 'ACCEPT_EULA=Y' -e 'MSSQL_SA_PASSWORD=Str0ngPa$$w0rd' -p 1433:1433 mcr.microsoft.com/mssql/server:2019-GDR1-ubuntu-16.04
```

