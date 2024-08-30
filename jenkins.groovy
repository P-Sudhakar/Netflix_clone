pipeline {
    agent any
    stages {
        stage('Checkout from Git') {
            steps {
                git 'https://github.com/P-Sudhakar/Netflix_clone'
            }
        }
        stage ('Build a Docker File') {
            steps {
                script{
                   withDockerRegistry(credentialsId: 'docker', toolName: 'docker'){   
                       sh "docker build -t netflix ."
                    }
                }
            }
        } 
        stage ('Tag A DockerImage') {
            steps {
                script{
                   withDockerRegistry(credentialsId: 'docker', toolName: 'docker'){   
                       sh "docker tag netflix sudhakarp1703/netflix:latest "
                   }      
                }               
            }
        }   
         stage ('Push DockerImage to the DockerHub') {
            steps {
                script{
                   withDockerRegistry(credentialsId: 'docker', toolName: 'docker'){   
                       sh "docker push sudhakarp1703/netflix:latest "
                   }      
                }               
            }
        }  
         stage ('Build a Container') {
            steps {
                sh "docker run -itd --name Netflix -p 8081:80 sudhakarp1703/netflix:latest"
            }               
        }
          stage ('create kube pod Netflix App') {
            steps {
                sh "kubectl apply -f deployment.yml"
            }               
        }
        stage ('create kube pod node port') {
            steps {
                sh "kubectl apply -f netflix_NodePort.yml"
            }               
        }
        stage ('create kube pod loadbalancer') {
            steps {
                sh "kubectl apply -f netflix_load.yml"
            }               
        }
    }  
}

