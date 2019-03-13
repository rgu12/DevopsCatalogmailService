/*
 * Author: Abhay Chrungoo <abhay@ziraffe.io>
 * Contributing HOWTO: TODO
 */

def runTest(String targetBranch, context) {
	node() {
		checkout scm
		this.runTestHandler( targetBranch, context)
	}
}

def runTestHandler(String targetBranch, context) {
	def exclusions= context.config.sonar.exclusions
	String  coverageExclusions = context.config.sonar.coverage_exclusions
	String  mavenPom = context.config.maven.pom ?: 'pom.xml'
	String  application = context.config.application
	String  qualityGate = context.config.sonar.quality_gate
	def appName = appName(context, targetBranch)
	String  mavenGoals = context.config.mavengoals.sonar?: 'clean test cobertura:cobertura sonar:sonar'
	try {
		withSonarQubeEnv('SONAR-main') {
			withEnv([
				"EXCLUSIONS=${exclusions}",
				"COVERAGE_EXCLUSIONS=${coverageExclusions}",
				"POM=${mavenPom}",
				"APPLICATION=${appName}",
				"MAVEN_GOAL=${mavenGoals}",
				"QUALITY_GATE=${qualityGate}"
			]) { sh '''source pipelines/scripts/functions && \\
						source pipelines/scripts/sonar.sh''' }
		}
	} catch (error) {
		echo "FAILURE: Sonar analysis ${error.message}"
		throw error
	} finally {
		archiveArtifacts "target/site/cobertura/coverage.xml"
		step([$class: 'WsCleanup', notFailBuild: true])
	}
}

def publishSplunk(String targetBranch, String epoch, context, handler) {
	echo "SKIPPING: Not publishing any Sonar results to splunk"
}
String name() {
	return "sonar"
}

return this;
