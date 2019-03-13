#!/bin/bash

source "${WORKSPACE}/pipelines/scripts/functions"   &>/dev/null


mvn -q 	\
	--non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec \
	-Dexec.executable="echo" \
	-Dexec.args='${project.version}' \
	2>/dev/null