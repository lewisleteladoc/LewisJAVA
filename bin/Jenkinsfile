pipeline {
    agent any

    parameters {
        choice(choices: 'FinnHub\nAlphaVantage', description: 'API', name: 'someApi')        
    }

    stages {
        stage('Load environment') {
            steps {
                load '.env.test.groovy'
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    def someApi = env.SOME_API
                    sh "docker build -t lewis-java-app:latest --build-arg SOME_API=${someApi} ."
                }
            }
        }

        stage('Docker Deploy') {
            steps {
                // 1. Remove the old container if it exists
                sh "docker rm -f my-running-lewis-java-app || true"
                
                // 2. Run the new container on port 8081
                sh "docker run -d --name my-running-lewis-java-app -p 8081:8081 lewis-java-app:latest"
                
                echo "Success! Your app is running on port 8081."
            }
        }
    }
}