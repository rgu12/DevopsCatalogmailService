#!/usr/bin/env bash

source "${WORKSPACE}/pipelines/scripts/functions"

set -ex

cf logout
cf login -a $BM_API -u $BM_USER -p $BM_PASS -o $BM_ORG -s $BM_ENV
cf delete ${APP} -f -r || echo "Failed to delete old application. It may not exist. Not Fatal, Onwards!"

cat pipelines/conf/manifest.yml

cf push ${APP} -f pipelines/conf/manifest.yml -p ${ZIPFILE} -t 180 -m ${MEMORY}

LOCAL_IP="https://${APP}.lbg.eu-gb.mybluemix.net/es-audit/v1.0"

check_running(){
  timeout=$TIMEOUT
  polltime=10

  echo "Attempting to hit server at $LOCAL_IP"
  while ! curl -f ${LOCAL_IP} &>/dev/null; do
    sleep $polltime
    time=$((time + polltime))
    if [ $time -gt $timeout ] ; then
      echo "Failed to start service in $timeout seconds. Aborting."
      exit 1
    fi
    echo "Waiting for service to be up..."
  done
}

check_running
