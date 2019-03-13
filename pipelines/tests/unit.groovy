/*
 * Author: Abhay Chrungoo <abhay@ziraffe.io>
 * Contributing HOWTO: TODO
 */

def runTest(String targetBranch, context) {
	node() {
		checkout scm
		this.runTestHandler(targetBranch, context)
	}
}

def runTestHandler(String targetBranch, context) {
	String  mavenGoals = context.config.mavengoals.unit?: 'clean test'
	String  mavenSettings = context.config.maven.settings
	String  mavenPom = context.config.maven.pom ?: 'pom.xml'
	try {
		sh """source pipelines/scripts/functions && \\
							mvn 	${mavenGoals} 	\\
									-s ${mavenSettings} \\
					 				-f ${mavenPom} \\
									"""
	} catch (error) {
		echo "FAILED: Unit tests ${error.message}"
		throw error
	} finally {
		archiveArtifacts '**/target/surefire-reports/*.*'
		junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
		step([$class: 'WsCleanup', notFailBuild: true])
	}
}

def publishSplunk(String targetBranch, String epoch, context, handler) {
	echo "SKIPPING: Not publishing any unit-test results to splunk"
}

String name() {
	return "Unit"
}

return this;
