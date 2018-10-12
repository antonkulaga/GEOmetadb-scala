#!/usr/bin/env bash
#does not really work, reported at https://github.com/dimitri/pgloader/issues/810
docker  run --rm --security-opt seccomp=unconfined --network host -v /data:/data -v $1:/import.sqlite dimitri/pgloader:latest pgloader --with "batch rows=10000" --with "batch size=10MB" --with "prefetch rows=50000"  /import.sqlite postgresql://postgres:changeme@localhost/postgres
