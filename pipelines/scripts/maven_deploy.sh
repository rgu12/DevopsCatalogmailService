#!/bin/bash

source "${WORKSPACE}/pipelines/scripts/functions"   &>/dev/null
 
set -ex
 
mvn build-helper:parse-version \
   versions:set \
   -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}${prereleaseID}${buildMetadata} \
   versions:commit \
   -s pipelines/conf/settings.xml
 
mvn deploy:deploy-file \
   -DpomFile=pom.xml \
   -Dpackaging=zip \
   -s pipelines/conf/settings.xml \
   -Durl="${url}" \
   -DrepositoryId=nexus-releases \
   -Dnexus.user="${NEXUS_USER}" \
   -Dnexus.password="${NEXUS_PASS}" \
   -Dclassifiers=config \
   -Dtypes=zip \
   -Dfile=target/${application}.zip \
   -Dfiles=target/${application}-config.zip
