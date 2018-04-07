# GEOmetadb-scala
scala code to deal with GEOmetadb.
Includes the code to deal with GEOmetadb sqlite (geo subprojects) as well as web-based UI to search it (web.server and web.client subprojects).

Running
------

To start the Web-based UI you should:
* download latest GEOMetadb Sqlite from https://gbnci-abcc.ncifcrf.gov/geo/GEOmetadb.sqlite.gz
* add pathy to it to application.conf
* start the web app by:
```
mill web.server.runLocal
```
*open localhost:8080
