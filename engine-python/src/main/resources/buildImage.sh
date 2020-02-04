#!/usr/bin/env bash
docker build -f Dockerfile-base -t "python-base:${version}" .
docker build -f Dockerfile -t "lds-aaas-python:${version}" .