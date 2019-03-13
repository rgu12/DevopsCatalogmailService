def pack(String targetBranch, String targetEnv, context) {
   node() {
       checkout scm
       this.packHandler(targetBranch, targetEnv, context)
   }
}

// This is for veracode - function has to be called runTest in current implementation
def runTest(String targetBranch, context) {
  packHandler(targetBranch, 'veracode', context)
}

def packHandler(String targetBranch, String targetEnv, context) {
   def mavenSettings  = context.config.maven.settings ?: 'pipelines/conf/settings.xml'
   def mavenPom = context.config.maven.pom ?: 'pom.xml'
   String application = context.application
   def mavenGoals = context.config.mavengoals.package?: "-U clean package"
   String targetCommit =  sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
   String prereleaseID = this.getReleaseIdentifier(targetBranch, targetEnv, targetCommit);
   String buildMetadata = ''

   try {
       dir('target'){deleteDir()}
            withEnv([
                "prereleaseID=${prereleaseID}",
                "application=${application}",
                "buildMetadata=${buildMetadata}",
                "mavenGoals=${mavenGoals}"
            ]){ sh 'pipelines/scripts/maven_package.sh'}

       dir('target'){
           if (targetEnv == 'veracode'){
               stash name: "artifacts", includes: "*.war"
           } else {
               archiveArtifacts '*.zip'
               stash name: "artifactStash", includes: '*.zip'
           }
       }
   } catch (error) {
       echo "Caught: ${error}"
       echo "Application Build failed"
       throw error
   } finally {
       step([$class: 'WsCleanup', notFailBuild: true])
   }
}

def publishNexus(String targetBranch, String targetEnv, context){
    def packageVersion
    node() {
        checkout scm
        stash  name: "pipelines-${context.application}-${targetBranch}", includes: '**'
        packageVersion = this.publishNexusHandler(targetBranch, targetEnv, context)
    }
    echo "post push packageVersion: ${packageVersion}";
    if(packageVersion) {
        node('master') {
            unstash "pipelines-${context.application}-${targetBranch}"
            promoteArtifact(packageVersion, context)
        }
    }
}

def publishNexusHandler(String targetBranch, String targetEnv, context){
   String artifactName
   String buildMetadata = ''
   String nexusURL = context.config.nexus.url ?: 'http://invalid.url/'
   String targetCommit =  sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
   String application = context.application
   String prereleaseID = this.getReleaseIdentifier(targetBranch, targetEnv, targetCommit);

   try {
       dir ('target'){
           deleteDir()
           unstash "artifactStash"
       }
       withCredentials([
           usernamePassword(credentialsId: 'nexus-uploader',
           passwordVariable: 'NEXUS_PASS',
           usernameVariable: 'NEXUS_USER')
       ]) {
           echo "PUBLISH: ${this.name()} artifact ${artifactName} to ${nexusURL} "
           withEnv([
               "prereleaseID=${prereleaseID}",
               "application=${application}",
               "buildMetadata=${buildMetadata}",
               "url=${nexusURL}"
           ]){ sh 'pipelines/scripts/maven_deploy.sh'}
       }
   } catch (error) {
    echo "Failed to publish artifact to Nexus"
    packageVersion = null
    } finally { }
    return packageVersion
}

def getPackageVersion() {
    def packageVersion = sh(returnStdout: true, script: '''source pipelines/scripts/functions && mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout''').trim()
    echo "packageVersion: ${packageVersion}"

    return packageVersion
}

String getReleaseIdentifier(String targetBranch, String targetEnv, String targetCommit){
    String prereleaseID='-SNAPSHOT'
    String branchIdentifier = targetBranch.take(2)
    switch(targetEnv) {
    case 'integration':
        if (targetBranch.startsWith('release')){
            prereleaseID = ""
        } else if(targetBranch == 'master') {
            prereleaseID = "-rc.${env.BUILD_NUMBER}.${targetCommit}"
        } else if(targetBranch.startsWith('hotfix')) {
            prereleaseID = "-hotfix.${env.BUILD_NUMBER}.${targetCommit}"
        } else {
            prereleaseID = "-${branchIdentifier}.${env.BUILD_NUMBER}.${targetCommit}"
        }
        break;
    case 'feature': prereleaseID = "-SNAPSHOT" ; break ;
    default: prereleaseID = "-SNAPSHOT" ; break ;
    }

   return prereleaseID;
}

def name() {
   return "Maven"
}

return this;
