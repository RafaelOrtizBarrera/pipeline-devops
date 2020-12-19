pipeline {
    agent any
    parameters { choice(name: 'TIPO_PIPELINE', choices: ['maven', 'gradle'], description: 'Determina con que herramienta se va a compilar') }
    
    stages {
        stage('Pipeline') {
            steps {
                script {
                  env.STAGE = ''
                  def script =  (params.TIPO_PIPELINE == 'maven') ? 'maven.groovy' :  'gradle.groovy';
                  echo 'pipeline seleccionado ' + script;
                  def tipoPipeline = load script;
                  tipoPipeline.call();
                }
            }
        }
    }

    post {
      success {
        slackSend color: "good", message: "[Rafael Ortiz][${env.JOB_NAME}][${params.TIPO_PIPELINE}] ejecución exitosa"
      }
      failure {
        slackSend color: "danger", message: "[Rafael Ortiz][${env.JOB_NAME}][${params.TIPO_PIPELINE}] ejecución fallida en stage [${env.STAGE}]"
      }
    }
}
