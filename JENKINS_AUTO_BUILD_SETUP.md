# Jenkins Auto-Build Configuration Guide

Complete guide to set up automatic builds in Jenkins.

---

## 🎯 Quick Setup - Poll SCM (5 Minutes)

This is the **easiest method** that works with any Git repository (local or remote).

### Step 1: Create/Configure Jenkins Job

1. Open Jenkins: `http://localhost:8080/jenkins`
2. Click **"New Item"**
3. Name: `auth-service-auto`
4. Type: **Pipeline**
5. Click **"OK"**

### Step 2: Configure Build Triggers

1. Scroll to **"Build Triggers"** section
2. ✅ Check **"Poll SCM"**
3. In the **Schedule** box, enter:
   ```
   H/5 * * * *
   ```
   This checks Git every 5 minutes.

4. Scroll to **"Pipeline"** section
5. **Definition**: `Pipeline script from SCM`
6. **SCM**: `Git`
7. **Repository URL**: 
   - Local: `file:///d:/my-prep/My-project/auth`
   - GitHub: `https://github.com/yourusername/auth.git`
   - GitLab: `https://gitlab.com/yourusername/auth.git`
8. **Branch**: `*/main` or `*/master`
9. **Script Path**: `Jenkinsfile`
10. Click **"Save"**

### Step 3: Test It!

```bash
# Make a change to any file
echo "// test auto build" >> README.md

# Commit and push
git add .
git commit -m "Test auto build"
git push

# Wait 5 minutes (or less) and Jenkins will auto-build!
```

---

## 📅 Poll SCM Schedule Syntax

```
┌───────────── minute (0-59)
│ ┌────────────── hour (0-23)
│ │ ┌─────────────── day of month (1-31)
│ │ │ ┌──────────────── month (1-12)
│ │ │ │ ┌───────────────── day of week (0-7, 0=Sunday)
│ │ │ │ │
* * * * *
```

### Common Schedules

| Schedule | Description |
|----------|-------------|
| `H/5 * * * *` | Every 5 minutes (recommended) |
| `H/2 * * * *` | Every 2 minutes |
| `H/10 * * * *` | Every 10 minutes |
| `H/15 * * * *` | Every 15 minutes |
| `H * * * *` | Every hour |
| `H H * * *` | Once per day |

**Note:** `H` = Hash. Jenkins spreads load evenly.

---

## 🌐 GitHub Webhook Setup (For GitHub Users)

### Prerequisites
- Jenkins must be publicly accessible (use ngrok for local testing)
- GitHub repository

### Step 1: Install ngrok (for Local Jenkins)

```bash
# Download ngrok from https://ngrok.com/download
# Start ngrok tunnel
ngrok http 8080

# You'll get a URL like: http://abc123.ngrok.io
```

### Step 2: Configure GitHub Webhook

1. Go to GitHub repository
2. **Settings** → **Webhooks** → **Add webhook**
3. **Payload URL**: 
   ```
   http://abc123.ngrok.io/jenkins/github-webhook/
   ```
4. **Content type**: `application/json`
5. **Secret**: (optional) generate secret token
6. **Which events?**: Select "Just the push event"
7. **Active**: ✅ Checked
8. Click **"Add webhook"**

### Step 3: Install GitHub Plugin in Jenkins

1. **Manage Jenkins** → **Manage Plugins** → **Available**
2. Search: `GitHub Integration`
3. Install and restart Jenkins

### Step 4: Configure Jenkins Job

1. Open your pipeline job
2. Click **"Configure"**
3. **Build Triggers**:
   - ✅ Check **"GitHub hook trigger for GITScm polling"**
4. **Pipeline** section:
   - **Definition**: `Pipeline script from SCM`
   - **SCM**: `Git`
   - **Repository URL**: `https://github.com/yourusername/auth.git`
   - **Credentials**: Add GitHub credentials
   - **Branch**: `*/main`
5. Click **"Save"**

### Step 5: Test

```bash
# Make a change
echo "test" >> README.md

# Commit and push
git add .
git commit -m "Test GitHub webhook"
git push origin main

# Jenkins builds IMMEDIATELY! ⚡
```

---

## 🦊 GitLab Webhook Setup (For GitLab Users)

### Step 1: Install GitLab Plugin

1. **Manage Jenkins** → **Manage Plugins**
2. Install **"GitLab Plugin"**
3. Restart Jenkins

### Step 2: Configure GitLab Connection in Jenkins

1. **Manage Jenkins** → **Configure System**
2. Find **"GitLab"** section
3. Click **"Add GitLab Connection"**
4. **Connection name**: `GitLab`
5. **GitLab host URL**: `https://gitlab.com`
6. **Credentials**: Add GitLab API token
   - Get token from GitLab: **Settings** → **Access Tokens**
   - Scope: `api`, `read_repository`
7. Click **"Test Connection"**
8. Save

### Step 3: Configure GitLab Webhook

1. Go to GitLab project
2. **Settings** → **Webhooks**
3. **URL**: 
   ```
   http://YOUR_JENKINS_URL:8080/jenkins/project/auth-service
   ```
4. **Secret Token**: Generate in Jenkins
5. **Trigger**: ✅ Push events
6. **SSL verification**: Disable (for local testing)
7. Click **"Add webhook"**
8. Click **"Test"** → **"Push events"**

### Step 4: Configure Jenkins Job

1. Pipeline job → **"Configure"**
2. **Build Triggers**:
   - ✅ Check **"Build when a change is pushed to GitLab"**
   - Note the **GitLab webhook URL**
3. Save

---

## 🔐 Bitbucket Setup

### For Bitbucket Cloud

1. Install **"Bitbucket Plugin"**
2. Repository → **Settings** → **Webhooks** → **Add webhook**
3. **URL**: `http://YOUR_JENKINS:8080/jenkins/bitbucket-hook/`
4. **Triggers**: Repository push

### For Bitbucket Server

1. Install **"Bitbucket Server Integration"** plugin
2. Configure similar to GitLab

---

## 🔄 Multi-Branch Pipeline (Advanced)

Auto-detect branches and build each one automatically!

### Create Multi-Branch Pipeline

1. **New Item** → **Multibranch Pipeline**
2. **Branch Sources** → **Add source** → **Git**
3. **Project Repository**: Enter Git URL
4. **Behaviors** → **Discover branches**
5. **Build Configuration**:
   - Mode: `by Jenkinsfile`
   - Script Path: `Jenkinsfile`
6. **Scan Multibranch Pipeline Triggers**:
   - ✅ **Periodically if not otherwise run**
   - Interval: `5 minutes`
7. Save

Now Jenkins will:
- ✅ Auto-discover all branches
- ✅ Build each branch separately
- ✅ Auto-delete jobs for deleted branches

---

## 📊 Comparison of Methods

| Method | Speed | Setup | Works Locally | Best For |
|--------|-------|-------|---------------|----------|
| **Poll SCM** | 2-5 min delay | ⭐ Easy | ✅ Yes | Local dev, any Git |
| **GitHub Webhook** | ⚡ Instant | ⭐⭐ Medium | ❌ No* | GitHub projects |
| **GitLab Webhook** | ⚡ Instant | ⭐⭐ Medium | ❌ No* | GitLab projects |
| **Multi-Branch** | 5 min delay | ⭐⭐⭐ Hard | ✅ Yes | Multiple branches |

*Requires public URL (use ngrok for local testing)

---

## 🧪 Testing Your Setup

### Verify Poll SCM is Working

1. Click your Jenkins job
2. Click **"Git Polling Log"** (left sidebar)
3. You should see:
   ```
   Started on Dec 20, 2025 2:30:00 AM
   Polling changes...
   No changes
   Done. Took 0.5 sec
   ```

### Verify Webhook is Working

1. GitHub/GitLab → Repository → Webhooks
2. Click on your webhook
3. Check **"Recent Deliveries"**
4. Should see successful responses (200 OK)

---

## 🐛 Troubleshooting

### Issue 1: Poll SCM Not Detecting Changes

**Check:**
- Git repository URL is correct
- Branch name is correct (`*/main` vs `*/master`)
- Jenkins has access to Git repo
- Git Polling Log shows no errors

**Solution:**
```bash
# In Jenkins job configuration
# Build Triggers → Poll SCM
# Schedule: H/5 * * * *
```

### Issue 2: GitHub Webhook Not Working

**Check:**
- Jenkins is publicly accessible
- Webhook URL ends with `/github-webhook/`
- GitHub webhook shows successful delivery
- "GitHub Integration" plugin is installed

**Solution:**
```bash
# Use ngrok for local Jenkins
ngrok http 8080

# Update GitHub webhook URL
http://YOUR_NGROK_URL/jenkins/github-webhook/
```

### Issue 3: Build Not Triggering

**Check Jenkins System Log:**
1. **Manage Jenkins** → **System Log**
2. Look for errors related to SCM or webhooks

**Force a build:**
```bash
# Click "Build Now" to test if pipeline works
# Then check why auto-trigger isn't working
```

---

## ✅ Recommended Setup for Your Project

### For Local Development (No GitHub/GitLab yet)

```groovy
// In Jenkins job configuration:
// Build Triggers: ✅ Poll SCM
// Schedule: H/5 * * * *
```

This checks your local Git repo every 5 minutes.

### For GitHub Project

```groovy
// In Jenkins job configuration:
// Build Triggers: ✅ GitHub hook trigger for GITScm polling
// + Add GitHub webhook
```

This triggers builds instantly on push.

---

## 📝 Quick Commands

```bash
# View Git polling log
curl http://localhost:8080/jenkins/job/auth-service/polling

# Trigger build manually via API
curl -X POST http://admin:TOKEN@localhost:8080/jenkins/job/auth-service/build

# Check webhook deliveries (GitHub)
# Go to: Repository → Settings → Webhooks → Recent Deliveries
```

---

## 🎯 Next Steps

1. ✅ Set up Poll SCM (5 minutes)
2. ✅ Test with a commit
3. ⏳ Add GitHub webhook (when ready for GitHub)
4. ⏳ Set up multi-branch pipeline (for multiple branches)

---

**Your Jenkins is now ready for automatic builds! Push code and watch the magic happen! 🚀**

