# GitHub Actions Documentation

## 🔄 CI/CD Workflows

This project uses GitHub Actions to automate code validation, testing, and quality checks before merging to main branches.

---

## 📋 Available Workflows

### 1. **CI/CD Pipeline** (`.github/workflows/ci-cd.yml`)

**Trigger Events:**
- Push to `main` or `develop` branches
- Pull request creation/updates
- Manual workflow dispatch

**Jobs:**
1. **Build & Test** (~10 minutes)
   - Compile code
   - Run 24 unit tests
   - Generate JaCoCo coverage report
   - Build JAR package
   - Upload artifacts

2. **Code Quality** (~5 minutes)
   - Code analysis (if configured)
   - Build verification

3. **Security Scan** (~5 minutes)
   - OWASP dependency check
   - Vulnerability detection

4. **Code Review Request** (~2 minutes) - PR only
   - Add summary comment
   - Highlight coverage metrics
   - List artifacts

5. **Deployment Ready Check** (~1 minute) - Main branch only
   - Verify all checks passed
   - Confirm deployment readiness

6. **Team Notification** (~1 minute)
   - Build status update

**Total Duration:** ~18 minutes per run

---

### 2. **Code Metrics** (`.github/workflows/code-metrics.yml`)

**Trigger Events:**
- Push to `main` or `develop`
- Pull requests
- Weekly schedule (Sunday 00:00 UTC)

**Jobs:**
1. Generate Code Metrics
   - Run tests
   - Generate JaCoCo reports
   - Create coverage summary
   - Comment on PR (if applicable)

**Artifacts:**
- Coverage reports (HTML, CSV, XML)

---

## 📊 Coverage Requirements

**Threshold:** 80% minimum code coverage

**Current Status:**
- ExpenseService: 78% ✅
- RevenueService: 82% ✅
- Service Layer: 80% ✅

**PR Blocking:** If coverage drops below 80%, PR review is still allowed but flagged.

---

## 🚀 First-Time Setup

### Step 1: GitHub Secrets (Optional for Enhanced Features)

Go to **Settings → Secrets and variables → Actions**

Optional secrets to add:
```
SLACK_WEBHOOK_URL = https://hooks.slack.com/...
EMAIL_SERVER = smtp.gmail.com
EMAIL_USERNAME = your-email@example.com
EMAIL_PASSWORD = app-specific-password
```

### Step 2: Branch Protection Rules

Go to **Settings → Branches**

Add rule for `main` branch:
```
✅ Require status checks to pass before merging
   - build-and-test
   - code-quality
   - security-scan

✅ Require 2 code review approvals

✅ Require branches to be up to date before merging

✅ Require conversation resolution

✅ Dismiss stale pull request approvals
```

### Step 3: Test the Pipeline

1. Create a test branch
2. Make a small change
3. Create a pull request
4. Watch GitHub Actions run automatically
5. Verify all checks pass ✅

---

## 📝 PR Template

When creating a PR, a template is automatically shown with:
- Type of change checkbox
- Related issue link
- Summary section
- Quality checklist (code, tests, docs, security)
- Coverage impact
- Test results
- Deployment notes

**Location:** `.github/pull_request_template.md`

---

## 👥 Code Ownership (CODEOWNERS)

Automatic reviewer assignment based on files changed:

**Backend Services:** @backend-lead, @backend-team  
**Frontend:** @frontend-team  
**Security:** @security-team  
**Database:** @database-team  
**Tests:** @qa-team  
**DevOps:** @devops-team  
**Documentation:** @tech-writer  
**Configuration:** @maintainer  

**Location:** `.github/CODEOWNERS`

---

## ✅ Workflow Status & Artifacts

### During PR:
- View workflow runs in **Pull Request → Checks** tab
- Click "Details" on each check to see logs
- Download artifacts (test results, coverage reports, JAR)
- See PR comment with summary

### After Merge:
- View workflow history in **Actions** tab
- Review coverage trends
- Monitor build reliability

### Artifacts Generated:
- `test-results/` - JUnit test reports
- `code-coverage-report/` - JaCoCo HTML/CSV/XML reports
- `dollar-tracker-jar/` - Compiled JAR package
- `dependency-check-report/` - Security scan results

---

## 🔧 Troubleshooting

### Workflow Fails: "Code coverage below 80%"

**Fix:**
1. Add more unit tests
2. Test untested code paths
3. Run locally: `mvn clean test jacoco:report`
4. View: `target/site/jacoco/index.html`

### Workflow Fails: "Test failures"

**Fix:**
1. Click workflow "Details"
2. Find failing test in logs
3. Run locally: `mvn test -Dtest=FailingTest`
4. Fix code and push

### Workflow Fails: "Dependency vulnerabilities"

**Fix:**
1. Review OWASP report
2. Upgrade vulnerable dependency
3. Run: `mvn clean test`
4. Verify no regressions

### Artifact Not Found

**Fix:**
1. Ensure build succeeds (check logs)
2. Verify file path in workflow
3. Check artifact retention settings

---

## 📊 Metrics Dashboard

**Check workflow metrics:**
- GitHub Actions tab → Workflow name → "Latest runs"
- See success rate and execution times
- Review trends over time

---

## 🎓 Best Practices

### For Developers:
- ✅ Keep PRs focused and small
- ✅ Maintain 80%+ code coverage
- ✅ Fill out PR template completely
- ✅ Wait for all checks to pass ✅
- ✅ Assign 2 reviewers

### For Reviewers:
- ✅ Review coverage report in PR comment
- ✅ Check GitHub Actions results
- ✅ Look for security issues
- ✅ Verify performance impact
- ✅ Approve when satisfied

### For Maintainers:
- ✅ Monitor workflow health
- ✅ Update dependencies regularly
- ✅ Review coverage trends
- ✅ Manage branch protection rules
- ✅ Archive old workflow runs

---

## 🔗 Quick Links

- **CI/CD Workflow:** `.github/workflows/ci-cd.yml`
- **Metrics Workflow:** `.github/workflows/code-metrics.yml`
- **PR Template:** `.github/pull_request_template.md`
- **CODEOWNERS:** `.github/CODEOWNERS`
- **Actions Dashboard:** https://github.com/[owner]/[repo]/actions
- **Project Docs:** `applicationprompt.md`

---

## 📞 Support

**Issues?**
- Check workflow logs in GitHub Actions
- Review artifact outputs
- File GitHub issue with `ci-cd` label
- Contact @devops-team

---

**Status:** ✅ Active  
**Last Updated:** March 25, 2026  
**Coverage Target:** 80%+ ✅
