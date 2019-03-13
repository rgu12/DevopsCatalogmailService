#!/usr/bin/env bash

source "${WORKSPACE}/pipelines/scripts/functions"

set -ex

cf logout
cf login -a $BM_API -u $BM_USER -p $BM_PASS -o $BM_ORG -s DEV 
#cf delete ${APP} -f -r || echo "Failed to delete old application. It may not exist. Not Fatal, Onwards!"
