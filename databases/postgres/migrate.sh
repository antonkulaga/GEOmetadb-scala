#!/usr/bin/env bash

#docker  run --rm --name pgloader --security-opt seccomp=unconfined --network host -v /pipelines/data:/pipelines/data dimitri/pgloader:latest
pgloader /pipelines/data/GEOmetadb.sqlite postgresql://postgres:changeme@localhost/sequencing