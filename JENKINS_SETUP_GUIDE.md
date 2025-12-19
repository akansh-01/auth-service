# Jenkins CI/CD Setup Guide for Healthcare Auth Service

This guide will help you set up Jenkins for automated building, testing, and deployment of your Auth Service.

---

## 🎯 What Jenkins Will Do

1. **Automated Builds** - Build your app on every commit
2. **Run Tests** - Execute unit tests and generate reports
3. **Code Coverage** - Check test coverage with JaCoCo
4. **Security Scanning** - OWASP dependency check
5. **Docker Image Creation** - Build and push Docker images
6. **Automated Deployment** - Deploy to dev/staging/production

---

## 🚀 Quick Start: Run Jenkins with Docker

### Step 1: Start Jenkins

```bash
# Start Jenkins server
docker-compose -f docker-compose.jenkins.yml up -d jenkins

# Check logs
docker logs -f jenkins
```

### Step 2: Get Initial Admin Password

```bash
# Get the initial admin password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Copy this password - you'll need it for the setup wizard.

### Step 3: Access Jenkins

Open your browser and go to:
```
http://localhost:8080
```

Paste the initial admin password when prompted.

### Step 4: Install Suggested Plugins

1. Click **"Install suggested plugins"**
2. Wait for installation to complete
3. Create your admin user
4. Set Jenkins URL: `http://localhost:8080`

---

## 🔧 Jenkins Configuration

### 1. Install Required Plugins

Go to **Manage Jenkins** → **Manage Plugins** → **Available**

Install these plugins:
- ✅ **Docker Pipeline** - For Docker operations
- ✅ **Git** - For Git integration (usually pre-installed)
- ✅ **Gradle** - For Gradle builds
- ✅ **JaCoCo** - For code coverage
- ✅ **HTML Publisher** - For publishing test reports
- ✅ **OWASP Dependency-Check** - For security scanning
- ✅ **Pipeline** - For pipeline jobs (usually pre-installed)
- ✅ **Blue Ocean** (Optional) - Modern UI for pipelines

After installation, click **"Restart Jenkins when installation is complete and no jobs are running"**

---

### 2. Configure Global Tools

Go to **Manage Jenkins** → **Global Tool Configuration**

#### Configure JDK

1. Click **Add JDK**
2. Name: `JDK21`
3. ✅ Check "Install automatically"
4. Select: **Java 21** from Oracle or Adopt OpenJDK

#### Configure Gradle

1. Click **Add Gradle**
2. Name: `Gradle8`
3. ✅ Check "Install automatically"
4. Select: **Gradle 8.5** or latest

#### Configure Docker (if not auto-detected)

1. Click **Add Docker**
2. Name: `Docker`
3. ✅ Check "Install automatically"
4. Select: **Download from docker.com**

Click **Save**

---

### 3. Configure Credentials

Go to **Manage Jenkins** → **Manage Credentials** → **(global)** → **Add Credentials**

#### Docker Hub Credentials

1. **Kind**: Username with password
2. **Username**: Your Docker Hub username
3. **Password**: Your Docker Hub password or access token
4. **ID**: `dockerhub-credentials`
5. **Description**: Docker Hub Credentials
6. Click **Create**

#### PostgreSQL Password (for tests)

1. **Kind**: Secret text
2. **Secret**: Your PostgreSQL password (e.g., `root`)
3. **ID**: `postgres-password`
4. **Description**: PostgreSQL Password
5. Click **Create**

#### Git Credentials (if using private repo)

1. **Kind**: Username with password or SSH key
2. **Username**: Your Git username
3. **Password**: Your Git password or PAT (Personal Access Token)
4. **ID**: `git-credentials`
5. **Description**: Git Credentials
6. Click **Create**

---

## 📋 Create Jenkins Pipeline Job

### Step 1: Create New Pipeline

1. Click **"New Item"**
2. Enter name: `auth-service-pipeline`
3. Select **"Pipeline"**
4. Click **OK**

### Step 2: Configure Pipeline

#### General Section

- ✅ **Description**: Healthcare Auth Service CI/CD Pipeline
- ✅ **Discard old builds**: Keep last 10 builds
- ✅ **GitHub project** (if applicable): Enter your repo URL

#### Build Triggers

Choose one or more:
- ✅ **Poll SCM**: `H/5 * * * *` (every 5 minutes)
- ✅ **GitHub hook trigger** (for automatic builds on push)

#### Pipeline Section

1. **Definition**: Pipeline script from SCM
2. **SCM**: Git
3. **Repository URL**: Your Git repository URL
   ```
   https://github.com/yourusername/auth-service.git
   ```
4. **Credentials**: Select your git credentials (if private repo)
5. **Branch Specifier**: `*/main` or `*/develop`
6. **Script Path**: `Jenkinsfile`

Click **Save**

---

## 🧪 Test Your Pipeline

### Step 3: Run First Build

1. Go to your pipeline job
2. Click **"Build Now"**
3. Click on the build number (e.g., #1)
4. Click **"Console Output"** to see logs

### Expected Stages

Your pipeline will run these stages:
1. ✅ **Checkout** - Clone the repository
2. ✅ **Build** - Compile the application
3. ✅ **Unit Tests** - Run tests
4. ✅ **Code Coverage** - Generate coverage report
5. ✅ **SonarQube Analysis** (if configured)
6. ✅ **Security Scan** - OWASP dependency check
7. ✅ **Build Docker Image** - Create Docker image
8. ✅ **Push to Docker Hub** (on main branch)
9. ✅ **Deploy** (based on branch)

---

## 📊 View Reports

After a successful build:

1. **Test Reports**
   - Click on the build
   - Click **"Test Result"**
   - View passed/failed tests

2. **Code Coverage**
   - Click on the build
   - Click **"Code Coverage"**
   - View line/branch coverage

3. **Security Report**
   - Click on the build
   - Click **"OWASP Dependency Check"**
   - View vulnerabilities

---

## 🔄 Branch-Based Deployment Strategy

Your Jenkinsfile is configured for:

| Branch | Action |
|--------|--------|
| `develop` | Build → Test → Deploy to DEV |
| `staging` | Build → Test → Deploy to STAGING |
| `main` | Build → Test → Docker Push → Deploy to PROD (with approval) |

---

## 🐳 Docker Integration

### Enable Docker in Jenkins Container

If you're running Jenkins in Docker, you need Docker-in-Docker:

```bash
# Enter Jenkins container
docker exec -it -u root jenkins bash

# Install Docker CLI
apt-get update
apt-get install -y docker.io

# Add jenkins user to docker group
usermod -aG docker jenkins

# Restart Jenkins container
docker restart jenkins
```

---

## 🎨 Blue Ocean UI (Optional)

For a better visual experience:

1. Install **Blue Ocean** plugin
2. Go to: `http://localhost:8080/blue`
3. View your pipelines in a modern UI

---

## 📧 Notifications Setup

### Email Notifications

1. Go to **Manage Jenkins** → **Configure System**
2. Find **Extended E-mail Notification**
3. Configure SMTP server:
   - **SMTP Server**: smtp.gmail.com
   - **Port**: 587
   - **Use SSL/TLS**: Yes
   - **Credentials**: Your email credentials

4. Add to Jenkinsfile:
```groovy
post {
    failure {
        emailext (
            subject: "Build Failed: ${currentBuild.fullDisplayName}",
            body: "Build failed. Check console output at ${env.BUILD_URL}",
            to: "your-email@example.com"
        )
    }
}
```

### Slack Notifications (Optional)

1. Install **Slack Notification** plugin
2. Configure Slack webhook
3. Add to Jenkinsfile:
```groovy
post {
    success {
        slackSend(
            color: 'good',
            message: "Build Successful: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        )
    }
}
```

---

## 🔒 Security Best Practices

### 1. Enable Matrix-Based Security

1. **Manage Jenkins** → **Configure Global Security**
2. Enable **Matrix-based security**
3. Add users and permissions

### 2. Use Credentials Plugin

- Never hardcode passwords
- Use Jenkins credentials
- Reference by ID in pipelines

### 3. Enable HTTPS

1. Generate SSL certificate
2. Configure Jenkins to use HTTPS
3. Redirect HTTP to HTTPS

### 4. Regular Updates

```bash
# Update Jenkins
docker-compose -f docker-compose.jenkins.yml pull
docker-compose -f docker-compose.jenkins.yml up -d
```

---

## 📝 Jenkinsfile Explained

Your current Jenkinsfile has these features:

### Environment Variables
```groovy
environment {
    DOCKER_IMAGE = "hungrycoders/auth-service"
    DOCKER_TAG = "${BUILD_NUMBER}"
}
```

### Stages
```groovy
stage('Build') {
    steps {
        sh './gradlew clean build -x test'
    }
}
```

### Post Actions
```groovy
post {
    always {
        cleanWs()  // Clean workspace
    }
    success {
        echo 'Success!'
    }
}
```

---

## 🧪 Testing Jenkins Locally

### Before Pushing to Git

Test your Jenkinsfile locally:

```bash
# Install Jenkins CLI
wget http://localhost:8080/jnlpJars/jenkins-cli.jar

# Validate Jenkinsfile
java -jar jenkins-cli.jar -s http://localhost:8080 \
  -auth admin:password \
  declarative-linter < Jenkinsfile
```

---

## 🎯 Gradle Configuration for Jenkins

Update your `build.gradle` to add testing and coverage:

```gradle
plugins {
    id 'jacoco'  // For code coverage
    id 'org.owasp.dependencycheck' version '8.4.0'  // For security
}

test {
    useJUnitPlatform()
    finalizedBy jacocoTestReport
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
    }
}

dependencyCheck {
    formats = ['HTML', 'JSON']
}
```

---

## 🚀 Advanced Features

### Multi-Branch Pipeline

For automatic pipeline creation per branch:

1. Create **"Multibranch Pipeline"** job
2. Configure branch sources (Git)
3. Jenkins will auto-discover branches with Jenkinsfile

### Parameterized Builds

Add parameters to your pipeline:

```groovy
parameters {
    choice(
        name: 'ENVIRONMENT',
        choices: ['dev', 'staging', 'prod'],
        description: 'Select deployment environment'
    )
}
```

### Parallel Execution

Run stages in parallel:

```groovy
stage('Parallel Tests') {
    parallel {
        stage('Unit Tests') {
            steps { sh './gradlew test' }
        }
        stage('Integration Tests') {
            steps { sh './gradlew integrationTest' }
        }
    }
}
```

---

## 📊 Monitoring Jenkins

### Health Checks

Access: `http://localhost:8080/manage/systemInfo`

### Metrics Plugin

1. Install **Metrics** plugin
2. Access: `http://localhost:8080/metrics`
3. View Jenkins performance metrics

---

## 🔧 Troubleshooting

### Issue 1: Permission Denied (Docker)

```bash
# Add jenkins user to docker group
docker exec -u root jenkins usermod -aG docker jenkins
docker restart jenkins
```

### Issue 2: Out of Memory

```yaml
# In docker-compose.jenkins.yml
environment:
  - JAVA_OPTS=-Xmx2048m -XX:MaxPermSize=512m
```

### Issue 3: Build Fails - Gradle Daemon

```bash
# Disable Gradle daemon for Jenkins
./gradlew build --no-daemon
```

### Issue 4: Port Already in Use

```bash
# Change Jenkins port in docker-compose.jenkins.yml
ports:
  - "8081:8080"  # Change to 8081
```

---

## ✅ Success Checklist

After setup, verify:

- [ ] Jenkins accessible at http://localhost:8080
- [ ] All required plugins installed
- [ ] JDK 21 and Gradle 8 configured
- [ ] Docker credentials added
- [ ] Pipeline job created
- [ ] First build successful
- [ ] Test reports visible
- [ ] Docker image created
- [ ] Notifications working (optional)

---

## 📚 Next Steps

1. **Set up SonarQube** for code quality
2. **Configure GitHub webhooks** for automatic builds
3. **Set up production deployment** pipeline
4. **Add integration tests** to pipeline
5. **Implement blue-green deployment**
6. **Set up monitoring** with Prometheus/Grafana

---

## 🎓 Learning Resources

- **Jenkins Documentation**: https://www.jenkins.io/doc/
- **Pipeline Syntax**: https://www.jenkins.io/doc/book/pipeline/syntax/
- **Best Practices**: https://www.jenkins.io/doc/book/pipeline/best-practices/
- **Docker Integration**: https://www.jenkins.io/doc/book/pipeline/docker/

---

**Your CI/CD pipeline is now ready! Every code push will trigger automated build, test, and deployment! 🚀**

