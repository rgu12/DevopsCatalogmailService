#!/bin/bash

source "${WORKSPACE}/pipelines/scripts/functions"   &>/dev/null

packagingGoal="${mavenGoals:-clean package}"

set -ex
mvn \
   build-helper:parse-version   \
   -Dartifact.version=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}${prereleaseID}${buildMetadata} \
   ${packagingGoal} \
   -s pipelines/conf/settings.xml
