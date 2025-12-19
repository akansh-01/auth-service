pipeline {
    agent any
    
    tools {
        jdk 'JDK21'  // Configure this in Jenkins Global Tool Configuration
        gradle 'Gradle8'  // Configure this in Jenkins Global Tool Configuration
    }
    
    environment {
        // Docker configuration
        DOCKER_IMAGE = "akanshproject/auth-service"
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials'
        
        // Database configuration for tests
        DB_URL = "jdbc:postgresql://localhost:5432/auth_project_test"
        DB_USERNAME = "postgres"
        DB_PASSWORD = credentials('postgres-password')
        
        // Application configuration
        SPRING_PROFILES_ACTIVE = "standalone"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from Git...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building the application...'
                sh './gradlew clean build -x test'
            }
        }
        
        stage('Unit Tests') {
            steps {
                echo 'Running unit tests...'
                sh './gradlew test'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                    publishHTML(target: [
                        reportDir: 'build/reports/tests/test',
                        reportFiles: 'index.html',
                        reportName: 'Test Report'
                    ])
                }
            }
        }
        
        stage('Code Coverage') {
            steps {
                echo 'Generating code coverage report...'
                sh './gradlew jacocoTestReport'
            }
            post {
                always {
                    jacoco(
                        execPattern: '**/build/jacoco/*.exec',
                        classPattern: '**/build/classes/java/main',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                echo 'Running SonarQube analysis...'
                script {
                    // Uncomment when SonarQube is set up
                    // withSonarQubeEnv('SonarQube') {
                    //     sh './gradlew sonarqube'
                    // }
                    echo 'SonarQube analysis skipped (not configured)'
                }
            }
        }
        
        stage('Security Scan') {
            steps {
                echo 'Running OWASP dependency check...'
                sh './gradlew dependencyCheckAnalyze'
            }
            post {
                always {
                    publishHTML(target: [
                        reportDir: 'build/reports/dependency-check',
                        reportFiles: 'dependency-check-report.html',
                        reportName: 'OWASP Dependency Check'
                    ])
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }
        
        stage('Push to Docker Hub') {
            when {
                branch 'main'
            }
            steps {
                echo 'Pushing Docker image to Docker Hub...'
                script {
                    docker.withRegistry('https://registry.hub.docker.com', DOCKER_CREDENTIALS_ID) {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }
        
        stage('Deploy to Dev') {
            when {
                branch 'develop'
            }
            steps {
                echo 'Deploying to Development environment...'
                sh '''
                    docker-compose -f docker-compose.dev.yml down
                    docker-compose -f docker-compose.dev.yml up -d
                '''
            }
        }
        
        stage('Deploy to Staging') {
            when {
                branch 'staging'
            }
            steps {
                echo 'Deploying to Staging environment...'
                sh '''
                    docker-compose -f docker-compose.staging.yml down
                    docker-compose -f docker-compose.staging.yml up -d
                '''
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                input message: 'Deploy to Production?', ok: 'Deploy'
                echo 'Deploying to Production environment...'
                sh '''
                    docker-compose -f docker-compose.prod.yml down
                    docker-compose -f docker-compose.prod.yml up -d
                '''
            }
        }
    }
    
    post {
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline executed successfully!'
            // Send notification (email, Slack, etc.)
        }
        failure {
            echo 'Pipeline failed!'
            // Send failure notification
        }
    }
}

