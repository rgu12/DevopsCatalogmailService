#!/bin/bash

source pipelines/scripts/functions

mvn ${MAVEN_GOAL} \
-Dcobertura.report.format=xml -Dcobertura.aggregate=true \
-Dsonar.java.coveragePlugin=cobertura \
-Dsonar.dynamicAnalysis=reuseReports \
-Dsonar.junit.reportsPath=target/surefire-reports \
-Dsonar.cobertura.reportPath=target/site/cobertura/coverage.xml \
-f ${POM} \
-s pipelines/conf/settings.xml \
-Dsonar.host.url=${SONAR_HOST_URL} \
-Dsonar.jdbc.url=${SONAR_JDBC_URL} \
-Dsonar.jdbc.username=${SONAR_JDBC_USERNAME} \
-Dsonar.jdbc.password=${SONAR_JDBC_PASSWORD} \
-Dsonar.login=${SONAR_LOGIN} \
-Dsonar.pasword=${SONAR_PASSWORD} \
-Dsonar.branch=${BRANCH_NAME} \
-Dsonar.projectKey=${APPLICATION} \
-Dsonar.skipPackageDesign=true \
-Dsonar.exclusions=${EXCLUSIONS} \
-Dsonar.coverage.exclusions=${COVERAGE_EXCLUSIONS} \
-Dsonar.scm.enabled=true \
-Dsonar.scm.provider=git \
-Dsonar.qualitygate=${QUALITY_GATE}  || exit_code=$?

# Remove long absolute paths from coverage xml. Dont let this fail the build
sed -i 's+'${WORKSPACE}'/++g'  target/site/cobertura/coverage.xml || true

error_generate "Test for Sonar Success"

