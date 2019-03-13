/*
 * Author: Abhay Chrungoo <abhay@ziraffe.io>
 */

def deploy(String targetBranch, context) {
	node() {
		unstash "pipelines"
		this.deployHandler(targetBranch, context)
	}
}

def deployHandler(String targetBranch, context) {
	def appName = appName(context, targetBranch)
	def artifactName
	def tokens
	def  memoryAllocation = context.config.environments.master.memory ?: '512M'
	def  bluemixDomain =  context.config.bluemix.domain ?: 'lbg.eu-gb.bluemix.net'
	def  bluemixAPI = context.config.bluemix.api ?: 'api.lbg.eu-gb.bluemix.net'
	def  bluemixEnv = context.config.environments.master.bluemix.env ?: 'INVALID'
	def  bluemixOrg = context.config.environments.master.bluemix.org ?: 'INVALID'
	def  bluemixCredentials = context.config.bluemix.credentials ?: 'bluemix-global-deployer'
	def  bluemixTimeout = context.config.bluemix.timeout ?: '120'
	switch (targetBranch) {
		case 'master':
			tokens = context.config.environments.master.tokens
			break
		default:
			tokens = context.config.environments.ci.tokens
			break
		}
	dir('artifacts'){
		unstash "artifactStash"
		artifactName = sh(script: "ls *.zip| grep -v config.zip | head -1", returnStdout: true).trim()
		sh "unzip ${artifactName} wlp/usr/servers/* "
		replaceTokens('wlp/usr/servers',tokens)
		sh "zip ${artifactName}  wlp -r"
	}
	withCredentials([
		usernamePassword(credentialsId: bluemixCredentials,
		passwordVariable: 'BM_PASS',
		usernameVariable: 'BM_USER')
	]) {
		withEnv([
			"BM_API=${bluemixAPI}",
			"BM_DOMAIN=${bluemixDomain}",
			"BM_ORG=${bluemixOrg}",
			"BM_ENV=${bluemixEnv}",
			"MEMORY=${memoryAllocation}",
			"APP=${appName}",
			"CF_HOME=${env.WORKSPACE}",
			"TIMEOUT=${bluemixTimeout}",
			"ZIPFILE=artifacts/${artifactName}"
		]) {
			try {
				sh 'pipelines/scripts/bluemix_deploy.sh'
			} catch (error) {
				echo "Deployment failed"
				throw error
			} finally {
				archiveArtifacts "artifacts/wlp/usr/servers/**/bootstrap.properties"
				archiveArtifacts "artifacts/wlp/usr/servers/**/jvm.options"
				archiveArtifacts "artifacts/wlp/usr/servers/**/server.env"
				step([$class: 'WsCleanup', notFailBuild: true])
			}
		}
	}
}

def purge(String targetBranch, context) {
	if(targetBranch.startsWith('master')){
		echo "SKIPPING: Wont terminate ${targetBranch} bluemix environment"
	}else{
		node() {
			unstash "pipelines"
			this.purgeHandler(targetBranch, context)
		}
	}
}

def purgeHandler(String targetBranch, context) {
	def appName = appName(context, targetBranch)
	def  bluemixDomain =  context.config.bluemix.domain ?: 'lbg.eu-gb.bluemix.net'
	def  bluemixAPI = context.config.bluemix.api ?: 'api.lbg.eu-gb.bluemix.net'
	def  bluemixEnv = context.config.environments.master.bluemix.env ?: 'INVALID'
	def  bluemixOrg = context.config.environments.master.bluemix.org ?: 'INVALID'
	def  bluemixCredentials = context.config.bluemix.credentials ?: 'bluemix-global-deployer'

	withCredentials([
		usernamePassword(credentialsId: 'bluemix-global-deployer',
		passwordVariable: 'BM_PASS',
		usernameVariable: 'BM_USER')
	]) {
		withEnv([
			"BM_API=${bluemixAPI}",
			"BM_DOMAIN=${bluemixDomain}",
			"BM_ORG=${bluemixOrg}",
			"BM_ENV=${bluemixEnv}",
			"APP=${appName}"
		]) {
			try {
				sh 'pipelines/scripts/bluemix_destroy.sh'
			} catch (error) {
				echo "Cleanup  failed. Not fatal, Onwards!!"
			} finally {
				step([$class: 'WsCleanup', notFailBuild: true])
			}
		}
	}
}

def name() {
	return "bluemix"
}

return this;
