# ============================================
# 儿童产品设计助手 - 一键推送并构建脚本（Windows PowerShell）
# 版本: 1.0.0
# 用途: 自动推送代码到GitHub并触发构建
# ============================================

# 项目配置
$GITHUB_USERNAME = "awlei"
$REPO_NAME = "child-product-design-assistant1"
$REMOTE_URL = "https://github.com/awlei/child-product-design-assistant1.git"
$PROJECT_NAME = "儿童产品设计助手"
$KEY_ALIAS = "design-assistant"
$KEYSTORE_FILE = "release-keystore.jks"

# 颜色函数
function Print-Success {
    param([string]$Message)
    Write-Host "✓ $Message" -ForegroundColor Green
}

function Print-Error {
    param([string]$Message)
    Write-Host "✗ $Message" -ForegroundColor Red
}

function Print-Info {
    param([string]$Message)
    Write-Host "ℹ $Message" -ForegroundColor Cyan
}

function Print-Warning {
    param([string]$Message)
    Write-Host "⚠ $Message" -ForegroundColor Yellow
}

function Print-Header {
    param([string]$Title)
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host "  $Title" -ForegroundColor Cyan
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host ""
}

# 主流程
function Main {
    Print-Header "儿童产品设计助手 - 一键推送并构建"
    
    Print-Info "目标仓库: $REMOTE_URL"
    Print-Info "GitHub用户: $GITHUB_USERNAME"
    Write-Host ""
    
    # 检查是否在项目目录
    if (-not (Test-Path "build.gradle.kts") -and -not (Test-Path "app/build.gradle.kts")) {
        Print-Error "未找到项目文件，请确保在项目根目录运行此脚本"
        exit 1
    fi
    
    Print-Success "项目目录验证通过"
    
    # 步骤1: 初始化Git仓库
    Print-Header "步骤 1: 初始化Git仓库"
    
    if (-not (Test-Path ".git")) {
        Print-Info "初始化Git仓库..."
        git init
        Print-Success "Git仓库初始化完成"
    } else {
        Print-Info "Git仓库已存在"
    }
    
    # 创建.gitignore
    if (-not (Test-Path ".gitignore")) {
        Print-Info "创建 .gitignore..."
        @"
# Build files
*.apk
*.ap_
*.aab
build/
.gradle/

# Keystore files
*.jks
*.keystore
keystore_base64.txt
keystore-passwords.txt

# IDE files
.idea/
*.iml
.vscode/

# OS files
.DS_Store
Thumbs.db

# Local config
local.properties
"@ | Out-File -FilePath ".gitignore" -Encoding UTF8
        Print-Success ".gitignore 创建完成"
    }
    
    # 步骤2: 生成签名密钥
    Print-Header "步骤 2: 生成签名密钥"
    
    if (-not (Test-Path $KEYSTORE_FILE)) {
        Print-Info "生成签名密钥..."
        Print-Warning "使用默认密码：Keystore=YourKeystorePassword123, Key=YourKeyPassword456"
        
        $result = & keytool -genkey -v `
            -keystore $KEYSTORE_FILE `
            -keyalg RSA `
            -keysize 2048 `
            -validity 10000 `
            -alias $KEY_ALIAS `
            -dname "CN=$PROJECT_NAME, OU=Development, O=YourCompany, L=YourCity, ST=YourState, C=CN" `
            -storepass "YourKeystorePassword123" `
            -keypass "YourKeyPassword456"
        
        if ($LASTEXITCODE -eq 0) {
            Print-Success "签名密钥生成完成"
        } else {
            Print-Error "签名密钥生成失败"
            exit 1
        }
    } else {
        Print-Info "签名密钥已存在，跳过生成"
    }
    
    # 步骤3: 转换密钥为Base64
    Print-Header "步骤 3: 转换密钥为Base64"
    
    $base64Output = [Convert]::ToBase64String([IO.File]::ReadAllBytes($KEYSTORE_FILE))
    $base64Output | Out-File -FilePath "keystore_base64.txt" -Encoding UTF8
    
    Print-Success "Base64 转换完成"
    Print-Info "已保存到: keystore_base64.txt"
    
    # 步骤4: 添加远程仓库
    Print-Header "步骤 4: 配置远程仓库"
    
    $remoteUrl = git remote get-url origin -ErrorAction SilentlyContinue
    if ($remoteUrl) {
        Print-Info "更新远程仓库地址..."
        git remote set-url origin $REMOTE_URL
    } else {
        Print-Info "添加远程仓库..."
        git remote add origin $REMOTE_URL
    }
    
    Print-Success "远程仓库配置完成: $REMOTE_URL"
    
    # 步骤5: 提交代码
    Print-Header "步骤 5: 提交代码"
    
    Print-Info "添加所有文件..."
    git add .
    
    Print-Info "创建提交..."
    $commitMessage = @"
feat: Professional Child Product Design Assistant - Initial Release

- Complete project structure for 4 product types
- Support for 5 international standards
- GPS028 design parameters
- GitHub Actions auto-build configuration
- Enhanced deployment scripts
"@
    git commit -m $commitMessage 2>$null
    if ($LASTEXITCODE -eq 0) {
        Print-Success "代码提交完成"
    } else {
        Print-Info "没有新的更改需要提交"
    }
    
    # 步骤6: 推送到GitHub
    Print-Header "步骤 6: 推送到GitHub"
    
    Print-Warning "⚠ 需要GitHub认证"
    Print-Info "Username: $GITHUB_USERNAME"
    Print-Info "Password: 请使用您的Personal Access Token"
    Write-Host ""
    
    # 设置main分支
    git branch -M main
    
    # 尝试推送
    Print-Info "正在推送代码..."
    
    $result = git push -u origin main 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Print-Success "✅ 代码推送成功！"
    } else {
        Print-Error "❌ 代码推送失败"
        Print-Info ""
        Print-Info "可能的原因:"
        Print-Info "1. GitHub仓库不存在"
        Print-Info "2. 认证失败（请使用Personal Access Token）"
        Print-Info "3. 网络问题"
        Print-Info ""
        Print-Warning "请检查后手动执行: git push -u origin main"
        exit 1
    }
    
    # 步骤7: 显示GitHub Secrets配置指南
    Print-Header "步骤 7: GitHub Secrets 配置"
    
    Print-Info "📋 请配置以下4个GitHub Secrets:"
    Write-Host ""
    Print-Success "Secret #1: KEYSTORE_BASE64"
    Print-Info "   值: keystore_base64.txt 文件的全部内容"
    Write-Host ""
    Print-Success "Secret #2: KEYSTORE_PASSWORD"
    Print-Info "   值: YourKeystorePassword123"
    Write-Host ""
    Print-Success "Secret #3: KEY_ALIAS"
    Print-Info "   值: $KEY_ALIAS"
    Write-Host ""
    Print-Success "Secret #4: KEY_PASSWORD"
    Print-Info "   值: YourKeyPassword456"
    Write-Host ""
    
    Print-Info "🔗 配置地址:"
    Print-Info "   https://github.com/$GITHUB_USERNAME/$REPO_NAME/settings/secrets/actions"
    Write-Host ""
    
    # 步骤8: 显示后续步骤
    Print-Header "后续步骤"
    
    Print-Success "✅ 代码已成功推送到GitHub！"
    Write-Host ""
    Print-Info "📚 接下来的操作:"
    Write-Host ""
    Write-Host "   1️⃣  配置GitHub Secrets（见上方）"
    Write-Host "   2️⃣  访问Actions页面: https://github.com/$GITHUB_USERNAME/$REPO_NAME/actions"
    Write-Host "   3️⃣  如未自动触发，点击 'Run workflow' 手动触发"
    Write-Host "   4️⃣  等待构建完成（约5-10分钟）"
    Write-Host "   5️⃣  在Artifacts中下载APK"
    Write-Host ""
    Print-Info "🔗 快速链接:"
    Write-Host "   • 仓库: https://github.com/$GITHUB_USERNAME/$REPO_NAME"
    Write-Host "   • Actions: https://github.com/$GITHUB_USERNAME/$REPO_NAME/actions"
    Write-Host "   • Secrets: https://github.com/$GITHUB_USERNAME/$REPO_NAME/settings/secrets/actions"
    Write-Host ""
    
    # 保存快速链接
    @"
儿童产品设计助手 - GitHub 快速链接
===================================

仓库地址: https://github.com/$GITHUB_USERNAME/$REPO_NAME
Actions监控: https://github.com/$GITHUB_USERNAME/$REPO_NAME/actions
Secrets配置: https://github.com/$GITHUB_USERNAME/$REPO_NAME/settings/secrets/actions

配置提示:
- 确保配置了4个GitHub Secrets
- 推送代码会自动触发构建
- 构建完成后在Actions页面下载APK

生成时间: $(Get-Date)
"@ | Out-File -FilePath "GITHUB_LINKS.txt" -Encoding UTF8
    
    Print-Info "💾 已创建 GITHUB_LINKS.txt 保存快速链接"
    Write-Host ""
    
    Print-Success "🎉 所有操作完成！"
    Print-Info "请立即配置GitHub Secrets以触发自动构建"
}

# 运行主函数
Main
