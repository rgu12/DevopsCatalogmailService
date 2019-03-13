@Library('workflowlib-sandbox@v5.5.1')
import com.lbg.workflow.sandbox.*

properties(defaultBuildJobProperties())

BuildHandlers handlers = new JavaBuildHandlers()
def configuration = "pipelines/conf/job-configuration.json"
def distroList = "LloydsOpenBanking-Commercial@sapient.com,lloydscjtdevops@sapient.com"
def appname = "mailApi"
Integer timeout = 45

invokeBuildPipelineHawk(appname, handlers, configuration, distroList, timeout)
