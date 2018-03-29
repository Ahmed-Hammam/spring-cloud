pipeline {
	agent {
	label "docker"
		}
	environment {
        	VERSION = VersionNumber([projectStartDate: '2017-08-01',versionNumberString: '${BUILDS_ALL_TIME}', versionPrefix: '']);
		}
	options {
   		 buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
 		 }

    stages {


	    stage ('Java Build') {

		steps{
			    configFileProvider([configFile(fileId: 'a6d88271-1f86-4a8b-8304-2cc2106c98b2', variable: 'MAVEN_SETTINGS')]) {
          	container('docker') {
                     withCredentials([usernamePassword(credentialsId: 'nexus-functional-user', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
				  sh """
					echo "$VERSION"
					mvn clean deploy -B -X -s $MAVEN_SETTINGS -Dmaven.test.skip=true
				  """
			    }
							}
					}
				}
		    post
 			{
 			success{
         	emailext attachLog: true,
		subject: "Jenkins Java Build $Version passed successfully", to:'amira.nagi@vodafone.com', body: """
		Hi All Kindly find the Java build logs attached to this file
		"""
 			}
         failure{
         	emailext attachLog: true,
		subject: "Jenkins Java Build $Version Failed", to:'aya.ghaafar1@vodafone.com,amira.nagi@vodafone.com', body: """
		Hi All Kindly find the java build logs attached to this file
		"""
         	 }
     	}

			}

			stage('Jobs-Service Docker'){

           steps{
			 sh """
			echo ${VERSION}
		    	sed -i s/VERSION/${VERSION}/ Deployment/jobs-service-deployment.yml
				"""
                    configFileProvider([configFile(fileId: 'a6d88271-1f86-4a8b-8304-2cc2106c98b2', variable: 'MAVEN_SETTINGS')]) {
          container('docker') {
                     withCredentials([usernamePassword(credentialsId: 'nexus-functional-user', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {

                        sh """
							  docker login nexus-registry.myvdf.aws.cps.vodafone.com --username ${USERNAME} --password ${PASSWORD}
							  cd ${WORKSPACE}/Code/jobs-service/src/main/docker/
							  cp ${WORKSPACE}/cacerts .
							  cp ${WORKSPACE}/pruebabt.pfx .
                              cp ${WORKSPACE}/Code/jobs-service/target/jobs-service-0.0.1-SNAPSHOT.jar .
                              docker build -t nexus-registry.myvdf.aws.cps.vodafone.com/jobs-service:v.${VERSION} .
                              docker push nexus-registry.myvdf.aws.cps.vodafone.com/jobs-service:v.${VERSION}
                              docker rmi 'nexus-registry.myvdf.aws.cps.vodafone.com/jobs-service:v.${VERSION}'
							  cd ${WORKSPACE}/
			     """
                    }
            }
			}
			}
	 post
 	      {

         	failure{
         		dockerfailed("Jobs Service build")
         	 }
     	}
        }

	stage('Deploy') {
	      agent {
			label "kubectl"
				}
	     steps {
			container('kubectl') {
				 sh """
				echo ${VERSION}
				sed -i s/VERSION/${VERSION}/ Deployment/jobs-service-deployment.yml
				echo "sed -i s/VERSION/${VERSION}/ Deployment/jobs-service-deployment.yml"
				"""
				sh " kubectl --namespace=test apply -f ./Deployment"
				sh "sleep 2"
				sh "kubectl get pods --namespace=test "
		}
	       }

   	 }


	}

		}

def dockersuccess(stagename){
	         emailext attachLog: true,
		subject: "Docker Stage passed successfully", to:'amira.nagi@vodafone.com', body: """
		Dears,
		 Kindly be informed that the job $stagename has passed successfully, please find the logs attached to this email.

		Thanks
		Deployment CoE
		"""
}
def dockerfailed(stagename){
	         emailext attachLog: true,
		subject: "Docker Stage Failed", to:'aya.ghaafar1@vodafone.com,amira.nagi@vodafone.com', body: """
		Dears,
		 Kindly be informed that the job $stagename has failed, please find the logs attached to this email.

		Thanks
		Deployment CoE
		"""
}
