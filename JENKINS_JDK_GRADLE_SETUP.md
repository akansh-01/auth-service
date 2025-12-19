# Jenkins Tool Configuration - JDK & Gradle Setup

Complete guide to configure JDK 21 and Gradle 8 in Jenkins.

---

## 🎯 Quick Setup (For Jenkins Docker with JDK21)

Your Jenkins container (`jenkins/jenkins:lts-jdk21`) already has JDK 21 built-in!

### Step 1: Configure JDK

1. **Manage Jenkins** → **Global Tool Configuration**
2. Scroll to **JDK** section
3. Click **"Add JDK"**
4. Configure:
   - **Name**: `JDK21` (must match Jenkinsfile)
   - ❌ **Uncheck** "Install automatically"
   - **JAVA_HOME**: `/opt/java/openjdk`
5. Click **"Apply"**

### Step 2: Configure Gradle

1. Still in **Global Tool Configuration**
2. Scroll to **Gradle** section
3. Click **"Add Gradle"**
4. Configure:
   - **Name**: `Gradle8` (must match Jenkinsfile)
   - ✅ **Check** "Install automatically"
   - Click **"Add Installer"** → Select **"Install from Gradle.org"**
   - **Version**: `8.5` (or latest 8.x)
5. Click **"Save"**

---

## 📋 Complete Configuration

### JDK 21 Configuration

| Field | Value |
|-------|-------|
| **Name** | `JDK21` |
| **Install automatically** | ❌ Unchecked |
| **JAVA_HOME** | `/opt/java/openjdk` |

### Gradle 8 Configuration

| Field | Value |
|-------|-------|
| **Name** | `Gradle8` |
| **Install automatically** | ✅ Checked |
| **Installer** | Install from Gradle.org |
| **Version** | `8.5` or `8.10` |

---

## 🐳 Verify Tools in Docker Container

```bash
# Check JDK
docker exec jenkins java -version
# Output: openjdk version "21.0.x"

# Check JAVA_HOME
docker exec jenkins echo $JAVA_HOME
# Output: /opt/java/openjdk

# Check if Gradle will be downloaded
docker exec jenkins ls /var/jenkins_home/tools/
# Gradle will be installed here on first build
```

---

## 🔧 Alternative: Use Automatic JDK Installer

If you want Jenkins to download JDK automatically:

### For Windows

1. **Name**: `JDK21`
2. ✅ **Check** "Install automatically"
3. **Add Installer** → **"Extract *.zip/*.tar.gz"**
4. **Download URL**: 
   ```
   https://download.java.net/java/GA/jdk21/fd2272bbf8e04c3dbaee13770090416c/35/GPL/openjdk-21_windows-x64_bin.zip
   ```
5. **Subdirectory**: `jdk-21`

### For Linux (in Docker)

1. **Name**: `JDK21`
2. ✅ **Check** "Install automatically"
3. **Add Installer** → **"Extract *.zip/*.tar.gz"**
4. **Download URL**:
   ```
   https://download.java.net/java/GA/jdk21/fd2272bbf8e04c3dbaee13770090416c/35/GPL/openjdk-21_linux-x64_bin.tar.gz
   ```
5. **Subdirectory**: `jdk-21`

---

## 🎨 What "Add Installer" Dropdown Should Show

When you click **"Add Installer"**, you should see:

```
Add Installer ▼
├── Extract *.zip/*.tar.gz           ← For manual archives
├── Install from Adoptium.net        ← For Eclipse Temurin
├── Install from java.sun.com        ← Deprecated
├── Run batch command                ← Manual (Windows)
└── Run shell command                ← Manual (Linux)
```

**Use "Extract *.zip/*.tar.gz" for automatic download and installation.**

---

## 🐛 Troubleshooting

### Issue 1: Only See "Run batch/shell command"

**Cause**: JDK installer plugin not installed or clicking wrong dropdown

**Solution**:
1. Install **"JDK Tool Plugin"**:
   - **Manage Jenkins** → **Manage Plugins** → **Available**
   - Search: `JDK Tool`
   - Install and restart
2. Or use the built-in JDK method above

### Issue 2: "JAVA_HOME is not defined"

**In Docker Container**:
```bash
# Verify JAVA_HOME
docker exec jenkins env | grep JAVA

# If not set, it's at:
/opt/java/openjdk
```

**In Jenkins Config**:
- Use `/opt/java/openjdk` as JAVA_HOME
- Don't use environment variable `$JAVA_HOME`

### Issue 3: Gradle Not Found

**Solution**:
```bash
# Gradle will be auto-downloaded on first build
# If you want to pre-install:
docker exec jenkins bash -c "cd /var/jenkins_home && wget https://services.gradle.org/distributions/gradle-8.5-bin.zip"
```

Or just let Jenkins download it automatically (recommended).

### Issue 4: Windows Path Issues

If using Windows Jenkins (not Docker):
```
# Wrong:
C:\Program Files\Java\jdk-21\

# Correct (use forward slashes):
C:/Program Files/Java/jdk-21
```

---

## ✅ Verification Checklist

After configuration, verify:

- [ ] JDK name is exactly `JDK21` (case-sensitive)
- [ ] Gradle name is exactly `Gradle8` (case-sensitive)
- [ ] JAVA_HOME points to valid directory
- [ ] Click "Save" at the bottom
- [ ] Build a test job to verify

---

## 🧪 Test Your Configuration

### Create a Test Pipeline

1. **New Item** → **Pipeline**
2. **Name**: `test-tools`
3. **Pipeline Script**:

```groovy
pipeline {
    agent any
    
    tools {
        jdk 'JDK21'
        gradle 'Gradle8'
    }
    
    stages {
        stage('Test Tools') {
            steps {
                sh 'java -version'
                sh 'gradle --version'
            }
        }
    }
}
```

4. Click **"Build Now"**
5. Check **Console Output**

**Expected Output**:
```
openjdk version "21.0.x"
Gradle 8.5
```

---

## 📝 Quick Reference

### For Docker Jenkins (Recommended)

| Tool | Name | Install Auto | Path/Version |
|------|------|--------------|--------------|
| **JDK** | `JDK21` | ❌ No | `/opt/java/openjdk` |
| **Gradle** | `Gradle8` | ✅ Yes | `8.5` or `8.10` |

### For Windows Jenkins

| Tool | Name | Install Auto | Path/Version |
|------|------|--------------|--------------|
| **JDK** | `JDK21` | ❌ No | `C:/Program Files/Java/jdk-21` |
| **Gradle** | `Gradle8` | ✅ Yes | `8.5` or `8.10` |

---

## 🎯 Common Mistakes to Avoid

❌ **Wrong**: Tool name doesn't match Jenkinsfile
```groovy
// Jenkinsfile says 'JDK21' but you named it 'Java21'
tools {
    jdk 'JDK21'  // Must match exactly!
}
```

❌ **Wrong**: Using backslashes on Windows
```
C:\Program Files\Java\jdk-21  // Wrong
```

✅ **Correct**: Using forward slashes
```
C:/Program Files/Java/jdk-21  // Correct
```

❌ **Wrong**: Extra spaces in names
```
Name: "JDK 21"  // Has space - won't match 'JDK21'
```

✅ **Correct**: No spaces
```
Name: "JDK21"  // Perfect
```

---

## 🚀 Next Steps

1. ✅ Configure JDK21 and Gradle8
2. ✅ Click "Save"
3. ✅ Run test pipeline
4. ✅ Build your auth-service project
5. ✅ Set up auto-builds (Poll SCM)

---

**Your Jenkins tools are now configured! Ready to build! 🎉**

