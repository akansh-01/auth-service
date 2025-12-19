# Jenkins Quick Start - 5 Minutes Setup

Get Jenkins running with your Auth Service in 5 minutes!

---

## 🚀 Super Quick Start

### Step 1: Start Jenkins (1 minute)

```bash
# Start Jenkins with Docker
docker-compose -f docker-compose.jenkins.yml up -d jenkins

# Wait 30 seconds, then get password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Copy the password displayed.

### Step 2: Setup Jenkins (2 minutes)

1. Open: http://localhost:8080
2. Paste the password
3. Click **"Install suggested plugins"**
4. Create admin user (username: `admin`, password: `admin123`)
5. Click **"Save and Finish"**

### Step 3: Install Extra Plugins (1 minute)

1. Go to **Manage Jenkins** → **Manage Plugins** → **Available**
2. Search and install:
   - `Docker Pipeline`
   - `JaCoCo`
   - `HTML Publisher`
3. Click **"Install without restart"**

### Step 4: Configure Tools (1 minute)

1. Go to **Manage Jenkins** → **Global Tool Configuration**

2. **Add JDK:**
   - Name: `JDK21`
   - ✅ Install automatically
   - Select: AdoptOpenJDK 21

3. **Add Gradle:**
   - Name: `Gradle8`
   - ✅ Install automatically
   - Select: Gradle 8.5

4. Click **"Save"**

### Step 5: Create Pipeline (<1 minute)

1. Click **"New Item"**
2. Name: `auth-service`
3. Type: **Pipeline**
4. Click **"OK"**
5. Scroll to **Pipeline** section
6. **Definition**: Pipeline script from SCM
7. **SCM**: Git
8. **Repository URL**: Your repo URL (or use local: `file:///workspace`)
9. **Script Path**: `Jenkinsfile`
10. Click **"Save"**

### Step 6: Run First Build!

1. Click **"Build Now"**
2. Watch the magic happen! ✨

---

## ✅ Verify Everything Works

After build completes:

```bash
# Check if Docker image was created
docker images | grep auth-service

# Check build artifacts
ls -la build/libs/

# Check test reports
ls -la build/reports/tests/test/

# Check coverage reports
ls -la build/reports/jacoco/test/html/
```

---

## 🎯 What Your Pipeline Does

1. ✅ **Checkout** - Gets your code
2. ✅ **Build** - Compiles with Gradle
3. ✅ **Test** - Runs unit tests
4. ✅ **Coverage** - Generates JaCoCo report
5. ✅ **Security** - OWASP dependency check
6. ✅ **Docker** - Builds Docker image

---

## 📊 View Results

### Test Report
- Go to build → **"Test Result"**
- See passed/failed tests

### Code Coverage
- Go to build → **"Code Coverage"**
- See line/branch coverage

### Security Scan
- Go to build → **"OWASP Dependency Check"**
- See vulnerabilities

---

## 🔧 Common Issues

### Issue: "Permission denied" on Docker

```bash
docker exec -u root jenkins usermod -aG docker jenkins
docker restart jenkins
```

### Issue: "Cannot find Gradle"

- Go to **Manage Jenkins** → **Global Tool Configuration**
- Verify Gradle is configured as `Gradle8`

### Issue: Build fails on tests

```bash
# Run tests locally first
./gradlew clean test

# Check database is running
docker ps | grep postgres
```

---

## 🎨 Optional: Blue Ocean UI

For a prettier interface:

1. Install **Blue Ocean** plugin
2. Go to: http://localhost:8080/blue
3. Enjoy the modern UI!

---

## 📧 Add Notifications (Optional)

### Slack Integration

1. Install **Slack Notification** plugin
2. Get Slack webhook URL
3. Configure in **Manage Jenkins** → **Configure System**
4. Builds will post to your Slack channel!

---

## 🚀 Next Steps

- [ ] Set up GitHub webhooks for auto-builds
- [ ] Add Docker Hub credentials
- [ ] Configure deployment to staging
- [ ] Set up SonarQube for code quality
- [ ] Add email notifications

---

## 📚 Full Documentation

For detailed setup, see **JENKINS_SETUP_GUIDE.md**

---

**That's it! Your CI/CD pipeline is running! 🎉**

Every git push will now trigger:
- ✅ Automated build
- ✅ Run tests
- ✅ Generate reports
- ✅ Create Docker image
- ✅ Security scanning

**Welcome to DevOps! 🚀**

