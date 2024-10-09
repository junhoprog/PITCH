pipeline {
    agent any

    environment {
        DOCKER_COMPOSE_DIR = "backend"
        MAIL_PASSWORD = credentials('mail_password')  // Jenkins에서 ENV 관리
        MAIL_ADDRESS = credentials('mail_address')
        CLIENT_ID = credentials('client_id')
        CLIENT_SECRET = credentials('client_secret')
    }

    triggers {
        pollSCM('* * * * *')  // GitHub Webhook과 함께 SCM 폴링 사용
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'develop', url: 'https://github.com/DAILAB-SUZY/PITCH.git'
            }
        }

        stage('Build Spring Boot Application') {
            steps {
                dir("${DOCKER_COMPOSE_DIR}") {
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('Docker Compose Up') {
            steps {
                dir("${DOCKER_COMPOSE_DIR}") {
                    sh """
                    export MAIL_PASSWORD=${MAIL_PASSWORD}
                    export CLIENT_ID=${CLIENT_ID}
                    export CLIENT_SECRET=${CLIENT_SECRET}
                    export MAIL_ADDRESS=${MAIL_ADDRESS}
                    docker-compose up --build -d
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
