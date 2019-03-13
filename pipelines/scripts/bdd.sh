#!/bin/bash -x

echo "Hostname:"
hostname

WORKSPACE=${WORKSPACE:-`pwd`}

source ${WORKSPACE}/pipelines/scripts/functions

set -x
echo APP is $APP
#echo API_ENDPOINT is $API_ENDPOINT
#find src/test/integration/ -type f | xargs sed -i 's#${API_ENDPOINT}#'${APP}'#g'


if [ -z "${zapReportDir}" ] ; then
      echo "No zapReportDir set, using 'zap-report' as default."
      zapReportDir=zap-report
fi

rm -rf ${zapReportDir} ; mkdir -p ${zapReportDir}

docker_terminate(){
    sudo docker stop $zapID || true
    sudo docker rm $zapID || true
}

trap docker_terminate EXIT ERR

LOCAL_IP="$(hostname -I | awk '{print $1}')"

check_running(){
  timeout=900
  polltime=3
  while ! nc -vz ${LOCAL_IP} ${1} &>/dev/null; do
    sleep $polltime
    time=$((time + polltime))
    if [ $time -gt $timeout ] ; then
      echo "Failed to start $2 in $timeout seconds. Aborting."
      exit 200
    fi
  done
  echo "$2 running on ${LOCAL_IP}:${1}"
}

echo "Starting ZAP proxy..."
zapID=$(docker run -d -u zap --name ${APP}-zap-$BUILD_NUMBER -p :8080 -i 10.112.159.88:40007/owasp-zap2docker:2.6.0 \
             zap-x.sh -daemon -host 0.0.0.0 -Xmx1024M \
                      -config api.disablekey=true \
                      -config api.addrs.addr.name=.* \
                      -config api.addrs.addr.regex=true)
echo "ZapID is ${zapID}"
ZAP_PORT=$(sudo docker port $zapID 8080 | sed 's/.*://')
echo "Zap port is ${ZAP_PORT}"
check_running ${ZAP_PORT} "ZAP"

export HTTP_PROXY=${LOCAL_IP}:${ZAP_PORT}
export HTTPS_PROXY=${LOCAL_IP}:${ZAP_PORT}
export http_proxy=${LOCAL_IP}:${ZAP_PORT}
export https_proxy=${LOCAL_IP}:${ZAP_PORT}
export no_proxy=localhost,127.0.0.1

echo "trying to run mvn test"
bash -c "mvn test-compile failsafe:integration-test -s pipelines/conf/settings.xml -Dapp=jenkins -Dservice=mock" -DAPP=$APP

sudo docker exec $zapID zap-cli report -f html -o report.html
sudo docker exec $zapID zap-cli report -f xml -o report.xml
sudo docker exec $zapID cat report.html >zap-report/report.html
sudo docker exec $zapID cat report.xml >zap-report/report.xml