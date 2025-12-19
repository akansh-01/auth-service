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
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }
        
        stage('Unit Tests') {
            steps {
                echo 'Running unit tests...'
                sh 'chmod +x gradlew'
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
                sh 'chmod +x gradlew'
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
                script {
                    try {
                        sh 'chmod +x gradlew'
                        sh './gradlew dependencyCheckAnalyze'
                    } catch (Exception e) {
                        echo "Security scan failed: ${e.message}"
                        echo "Continuing build..."
                    }
                }
            }
        }
        
        // Docker stages commented out - enable when Docker is set up
        /*
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
        */
    }
    
    post {
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

