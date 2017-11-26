#!groovy

def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    env.IMAGE_NAME    = config.get("imageName")
    env.TEST_IMAGE    = config.get("testImage", false)
    env.PUBLISH_IMAGE = config.get("publishImage", false)

    properties([
        buildDiscarder(logRotator(numToKeepStr: '10'))
    ])


    node('docker') {
        timestamps {
            stage('SCM'){
                deleteDir()
                def scmVars = checkout scm
                env.COMMIT_SHA = scmVars.GIT_COMMIT.substring(0,7)

                def imagename_commitsha   = "${env.IMAGE_NAME}" + "-" + "${env.COMMIT_SHA}"
		env.IMAGE_TAG             = "${imagename_commitsha}"  + ":" + config.get("imageTag")
            }
                
            stage('create image') {
                dir("${env.WORKSPACE}/16_shared_library/") { 
                    dockerTasks.createImage()
                }
            }
                
            if ( env.TEST_IMAGE == "true" ) {
                stage('test image') {
                    dockerTasks.validateImage()
                }
            }
                
            if ( env.PUBLISH_IMAGE == "false" ) {
                println "Image not deposited in registry"
            }
        }   
    }
}
