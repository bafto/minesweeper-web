#!/bin/sh

/app/minesweeper-web-1.0-SNAPSHOT/bin/minesweeper-web \
	-Dplay.http.secret.key=$APP_SECRET \
	-Dplay.filters.hosts.allowed.0=minesweeper.bafto.dev \
	-Dplay.filters.hosts.allowed.1=localhost:9000
