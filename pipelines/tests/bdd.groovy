def runTest(String targetBranch, context) {
	String label = context.config.builder.label
	node(label) {
		checkout scm
		echo "Checked out SCM"
		this.runTestHandler(targetBranch, context)
	}
}

def runTestHandler(String targetBranch, context) {
	def app = appName(context,targetBranch)
	def scenarioPassThreshold = context.config.bdd.percent_scenarios ?: '100'
	def zapReportDir = context.config.zap.resultdir ?: "zap-report"
	def zapQualityGate = context.config.zap.qualityGate ?: 'invalid-gate'
	echo "BDD threshold set to ${scenarioPassThreshold}"

	try {
		withEnv([
				"APP=${app}",
				"zapReportDir=${zapReportDir}",
				"RESULTSDIR=target/surefire-reports"]) {
			sh 'rm -rf ${RESULTSDIR} && mkdir -p ${RESULTSDIR}'

			echo "BDD test result directory ${RESULTSDIR}"

			sh "pipelines/scripts/bdd.sh"

			step([$class               : 'CucumberReportPublisher',
				  failedFeaturesNumber : 99999999999,
				  failedScenariosNumber: 9999999999,
				  failedStepsNumber    : 99999999999,
				  fileExcludePattern   : '',
				  fileIncludePattern   : '*.json',
				  jsonReportDirectory  : RESULTSDIR,
				  parallelTesting      : false,
				  pendingStepsNumber   : 99999999999,
				  skippedStepsNumber   : 99999999999,
				  trendsLimit          : 0,
				  undefinedStepsNumber : 99999999999
			])
			dir(RESULTSDIR) {
				sh ('pwd; ls -lah')
				stash(name: "BDD-${context.application}-${targetBranch}", includes: '*.json')
				withEnv(["SCENARIO_PASS_THRESHOLD=${scenarioPassThreshold}"]) {
					sh "${env.WORKSPACE}/pipelines/scripts/bdd-pass-threshold-checker.sh"
				}
			}
			dir(zapReportDir){
				archiveArtifacts allowEmptyArchive: false,
						artifacts: "*", fingerprint: false, onlyIfSuccessful: false
				sh "ls -la"
			}
		}
	} catch (error) {
		echo "FAILED; Cucumber run, message: ${error.message}"
		throw error
	}
}

def publishSplunk(String targetBranch, String epoch, context, handler){
	def appname = appName(context, targetBranch)
	String  journey = context.config.journey?: 'INVALID'
	def splunkReportDir = "${context.config.splunk.reportdir}"
	echo "PUBLISH: ${this.name()} ${appname} reports to Splunk"
	sh 'rm -rf j2/bddReports'
	dir ('j2/bddReports') {
		unstash "BDD-${context.application}-${targetBranch}"
		sh 'tree -L 3'
		sh """  mkdir -p ${journey}/bdd/${appname}/${epoch}  && \\
                cp *.json ${journey}/bdd/${appname}/${epoch}
            """
		handler.SCP('*.json',
				splunkReportDir)
		handler.RSYNC(journey,
				'/apps/reports/')
	}
}

String name() {
	return "BDD"
}

return this;
