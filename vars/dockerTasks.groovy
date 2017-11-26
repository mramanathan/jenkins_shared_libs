#!groovy

def createImage() {

    def build_cmd = "docker build -t ${env.IMAGE_TAG} -f Dockerfile ."

    println "createImage: Starting docker build with cmd, ${build_cmd}"
    sh(returnStdout: true, script: "${build_cmd}")
}

def stopContainer() {
    def id = sh (returnStdout: true,
                 script: """
                            docker ps --filter ancestor=${env.IMAGE_TAG} --format "{{ .ID }}"
                          """
                ).trim()

    sh "docker container stop ${id}"
}

def startContainer() {
    
    def start_container = "docker container run --rm -d --name "
    start_container += "${env.IMAGE_NAME} -p 80:80 ${env.IMAGE_TAG}"

    println "startContainer: Starting a fresh container using image, ${env.IMAGE_TAG}"
    sh(returnStdout: true, script: "${start_container}")
}

def validateImage() {

    def container_home = "http://localhost:80/health/index.html"
    def curl_cmd       = "curl -o status.txt ${container_home}"

    try {
        startContainer()

        sh(returnStdout: true, script: "${curl_cmd}")
        def status = readFile "${env.WORKSPACE}/status.txt"

        if ( status.trim().contains("Healthy") ) {
            println "Docker image, ${env.IMAGE_TAG} has been successfully built and tested"
	    stopContainer()
        } else {
            println "Docker image, ${env.IMAGE_TAG}, has problems in build or test stage"
	    stopContainer()
        }
    } catch (Exception error) {
        println "validateImage: Image validation failed!"
    }
}

return this;
