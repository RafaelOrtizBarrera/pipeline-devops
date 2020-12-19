def call(){

  stage('compile') {
    env.STAGE = 'compile'
    sh 'mvn clean compile -e'
  }
  stage('test') {
    env.STAGE = 'test'
    sh 'mvn clean test -e'
  }
  stage('jar') {
    env.STAGE = 'jar'
    sh 'mvn clean package -e'
  }
  stage('sonar'){
    env.STAGE = 'sonar'
    withSonarQubeEnv(installationName: 'sonar-local') { // You can override the credential to be used
      sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
    }
  }
  stage('run'){
    env.STAGE = 'run'
    withEnv(['JENKINS_NODE_COOKIE=dontkillme']) {
      sh 'java -version'
      sh """
        nohup java -jar build/DevOpsUsach2020-0.0.1.jar &
      """
    }
  }
  stage('test api'){
    env.STAGE = 'test api'
    echo 'Esperando a que inicie el servidor'
    sleep(time: 10, unit: "SECONDS")
    script {
      final String url = "http://localhost:8082/rest/mscovid/test?msg=testing"
      final String response = sh(script: "curl -X GET $url", returnStdout: true).trim()

      echo response
    }
  }
  stage('nexus'){
    env.STAGE = 'nexus'
    nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus-rafa', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: '/Users/rafael/cursos-dev/diplomado-devops/ci-cd/ejemplo-gradle/build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
  }
}

return this;