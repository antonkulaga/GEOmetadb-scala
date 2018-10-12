# GEOmetadb-scala
scala code to deal with GEOmetadb.
Includes the code to deal with GEOmetadb sqlite (geo subprojects) as well as web-based UI to search it (web.server and web.client subprojects).

Starting postgres containers
-------------------

If no postgres database is running you can start the default one.
You should use docker swarm, that you can initialize with:
```bash
docker swarm init
```
and then just run 
```bash
docker stack deploy -c databases/postgres/docker-compose.yml geometa
```

Getting latest GeometaDB
------------------------

You can download Geometadb sqlite database:
```bash
databases/postgres/download.sh
```
and then install pgloader and run migration script (postgres containers should be already running)
```bash
sudo apt install pgloader
databases/postgres/migrate.sh
```

Running
------

To start the Web-based UI you should:
* download latest GEOMetadb Sqlite from https://gbnci-abcc.ncifcrf.gov/geo/GEOmetadb.sqlite.gz
* add path to it to application.conf
* start the web app by:
```bash
mill web.server.runLocal
```
*open localhost:8080
