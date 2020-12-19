


def call(){
  pipeline {
    agent any
    parameters { 
      choice(name: 'TIPO_PIPELINE', choices: ['maven', 'gradle'], description: 'Determina con que herramienta se va a compilar') 
      //string(name: 'STAGE', defaultValue: '', description: 'Seleccionar stages a ejecutar. Vacio para ejecutar todos')
    }
    
    stages {
      stage('Pipeline') {
        steps {
          script {
            //try {
              env.STAGE = ''
              echo 'pipeline seleccionado ' + script
              //def stagesReq = params.STAGE.split(";")
              //echo 'stages ' + stagesReq 
              if(params.TIPO_PIPELINE == 'maven'){
                maven.call()
              } else {
                gradle.call()
              }
            /*}
            catch (exc) {
              echo 'Error al eleigit tipo pipeline ' + exc
            }*/
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
}

return this;
