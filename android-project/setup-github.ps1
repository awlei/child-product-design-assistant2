# ============================================
# 儿童产品设计助手 - 自动化部署脚本 (Windows PowerShell)
# 版本: 1.0.0
# 用途: 一键初始化Git仓库、生成密钥、推送到GitHub
# ============================================

# 项目配置
$PROJECT_NAME = "child-product-design-assistant"
$APP_NAME = "儿童产品设计助手"
$PACKAGE_NAME = "com.design.assistant"
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

# 检查必要工具
function Check-Prerequisites {
    Print-Header "检查必要工具"
    
    $missingTools = @()
    
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        $missingTools += "java"
    }
    
    if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
        $missingTools += "git"
    }
    
    if (-not (Get-Command keytool -ErrorAction SilentlyContinue)) {
        $missingTools += "keytool"
    }
    
    if ($missingTools.Count -gt 0) {
        Print-Error "缺少必要工具: $($missingTools -join ', ')"
        Print-Info "请先安装这些工具后再继续"
        exit 1
    }
    
    Print-Success "所有必要工具已安装"
    java -version
    git --version
    keytool -version
}

# 生成签名密钥
function Generate-Keystore {
    Print-Header "步骤 1: 生成签名密钥"
    
    if (Test-Path $KEYSTORE_FILE) {
        Print-Warning "密钥文件已存在: $KEYSTORE_FILE"
        $response = Read-Host "是否重新生成？(y/N)"
        if ($response -ne 'y' -and $response -ne 'Y') {
            Print-Info "使用现有密钥文件"
            return
        }
        Remove-Item $KEYSTORE_FILE
    }
    
    Print-Info "开始生成签名密钥..."
    Print-Warning "请记住您设置的密码，这些信息需要配置到GitHub Secrets中"
    Print-Info "默认密码: Keystore密码=YourKeystorePassword123, Key密码=YourKeyPassword456"
    
    $response = Read-Host "使用默认密码？(Y/n)"
    if ($response -ne 'n' -and $response -ne 'N') {
        $STORE_PASS = "YourKeystorePassword123"
        $KEY_PASS = "YourKeyPassword456"
    } else {
        $STORE_PASS = Read-Host "输入Keystore密码" -AsSecureString
        $KEY_PASS = Read-Host "输入Key密码" -AsSecureString
        $STORE_PASS = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($STORE_PASS))
        $KEY_PASS = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($KEY_PASS))
    }
    
    # 生成密钥
    $dname = "CN=$APP_NAME, OU=Development, O=YourCompany, L=YourCity, ST=YourState, C=CN"
    
    $result = & keytool -genkey -v `
        -keystore $KEYSTORE_FILE `
        -keyalg RSA `
        -keysize 2048 `
        -validity 10000 `
        -alias $KEY_ALIAS `
        -dname $dname `
        -storepass $STORE_PASS `
        -keypass $KEY_PASS
    
    if ($LASTEXITCODE -eq 0) {
        Print-Success "密钥文件生成成功: $KEYSTORE_FILE"
        Print-Warning "⚠ 重要提示:"
        Print-Warning "   - Keystore 密码: $STORE_PASS"
        Print-Warning "   - Key 密码: $KEY_PASS"
        Print-Warning "   - Key 别名: $KEY_ALIAS"
        Print-Warning "   请将这些信息保存好，稍后需要配置到 GitHub Secrets"
        
        # 保存密码到临时文件（方便配置Secrets）
        @"
Keystore Password: $STORE_PASS
Key Password: $KEY_PASS
Key Alias: $KEY_ALIAS
"@ | Out-File -FilePath "keystore-passwords.txt" -Encoding UTF8
        Print-Info "密码信息已保存到: keystore-passwords.txt"
    } else {
        Print-Error "密钥生成失败"
        exit 1
    }
}

# 转换为Base64
function Convert-ToBase64 {
    Print-Header "步骤 2: 转换密钥为Base64"
    
    if (-not (Test-Path $KEYSTORE_FILE)) {
        Print-Error "密钥文件不存在，请先生成密钥"
        exit 1
    }
    
    Print-Info "正在转换密钥文件..."
    
    $base64Output = [Convert]::ToBase64String([IO.File]::ReadAllBytes($KEYSTORE_FILE))
    
    # 保存到文件
    $base64Output | Out-File -FilePath "keystore_base64.txt" -Encoding UTF8
    
    Print-Success "Base64 转换完成"
    Print-Info "已保存到: keystore_base64.txt"
    
    # 显示前20个字符
    Print-Info "Base64 前20个字符: $($base64Output.Substring(0, [Math]::Min(20, $base64Output.Length)))..."
}

# 初始化Git仓库
function Init-GitRepo {
    Print-Header "步骤 3: 初始化Git仓库"
    
    if (Test-Path ".git") {
        Print-Warning "Git仓库已存在"
        $response = Read-Host "是否重新初始化？(y/N)"
        if ($response -ne 'y' -and $response -ne 'Y') {
            return
        }
        Remove-Item -Recurse -Force .git
    }
    
    Print-Info "初始化Git仓库..."
    git init
    Print-Success "Git仓库初始化完成"
    
    # 创建.gitignore
    if (-not (Test-Path ".gitignore")) {
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
        Print-Success "已创建 .gitignore"
    }
}

# 提交文件
function Commit-Files {
    Print-Header "步骤 4: 提交文件到Git"
    
    Print-Info "添加所有文件..."
    git add .
    
    Print-Info "创建首次提交..."
    $commitMessage = @"
Initial commit: Professional Child Product Design Assistant

- Complete project structure
- Core architecture for 4 product types
- 5 international standards support
- GPS028 design parameters
- GitHub Actions auto-build configuration
"@
    git commit -m $commitMessage
    
    Print-Success "文件提交完成"
}

# 配置远程仓库
function Setup-RemoteRepo {
    Print-Header "步骤 5: 配置GitHub远程仓库"
    
    Print-Info "请提供您的GitHub用户名:"
    $GITHUB_USERNAME = Read-Host "GitHub 用户名"
    
    if ([string]::IsNullOrWhiteSpace($GITHUB_USERNAME)) {
        Print-Error "GitHub用户名不能为空"
        exit 1
    }
    
    $REMOTE_URL = "https://github.com/$GITHUB_USERNAME/$PROJECT_NAME.git"
    
    Print-Info "远程仓库地址: $REMOTE_URL"
    Print-Warning "⚠ 请确保您已在GitHub上创建了仓库: $PROJECT_NAME"
    $response = Read-Host "仓库已创建吗？(y/N)"
    
    if ($response -ne 'y' -and $response -ne 'Y') {
        Print-Info "请先在GitHub上创建仓库，然后重新运行此脚本"
        exit 1
    }
    
    # 检查是否已存在远程仓库
    $remoteUrl = git remote get-url origin -ErrorAction SilentlyContinue
    if ($remoteUrl) {
        git remote set-url origin $REMOTE_URL
        Print-Success "已更新远程仓库地址"
    } else {
        git remote add origin $REMOTE_URL
        Print-Success "已添加远程仓库"
    }
}

# 推送到GitHub
function Push-ToGitHub {
    Print-Header "步骤 6: 推送到GitHub"
    
    Print-Info "正在推送代码..."
    
    # 设置main分支
    git branch -M main
    
    # 尝试推送
    $result = git push -u origin main 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Print-Success "代码推送成功！"
    } else {
        Print-Error "推送失败"
        Print-Info "可能的原因:"
        Print-Info "1. GitHub仓库不存在"
        Print-Info "2. 需要GitHub身份验证"
        Print-Info "3. 网络问题"
        Print-Warning "请检查后手动执行: git push -u origin main"
    }
}

# 显示GitHub Secrets配置指南
function Show-SecretsGuide {
    Print-Header "步骤 7: GitHub Secrets 配置指南"
    
    Print-Info "请按以下步骤配置GitHub Secrets:"
    Write-Host ""
    Write-Host "1. 访问您的GitHub仓库"
    Write-Host "2. 点击 Settings → Secrets and variables → Actions"
    Write-Host "3. 点击 'New repository secret'"
    Write-Host "4. 添加以下4个密钥:"
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
    
    # 读取base64文件
    if (Test-Path "keystore_base64.txt") {
        $base64Content = Get-Content "keystore_base64.txt" -Raw
        Print-Info "KEYSTORE_BASE64 的前50个字符:"
        Write-Host "$($base64Content.Substring(0, [Math]::Min(50, $base64Content.Length)))..."
    }
    
    # 读取密码文件
    if (Test-Path "keystore-passwords.txt") {
        Print-Info "密码信息已保存到: keystore-passwords.txt"
    }
}

# 显示后续步骤
function Show-NextSteps {
    Print-Header "后续步骤"
    
    Print-Success "✅ 自动化部署完成！"
    Write-Host ""
    Print-Info "接下来的操作:"
    Write-Host "1. ✅ 代码已推送到GitHub"
    Write-Host "2. 📝 配置GitHub Secrets（见上方指南）"
    Write-Host "3. ⚙️ GitHub Actions会自动开始构建APK"
    Write-Host "4. 📥 在Actions页面下载构建好的APK"
    Write-Host ""
    Print-Info "查看构建状态:"
    Write-Host "   https://github.com/$GITHUB_USERNAME/$PROJECT_NAME/actions"
    Write-Host ""
    Print-Info "本地构建测试:"
    Write-Host "   gradlew.bat assembleDebug"
    Write-Host ""
    Print-Success "祝您使用愉快！🎉"
}

# 主函数
function Main {
    Print-Header "儿童产品设计助手 - 自动化部署"
    
    Print-Info "本脚本将帮助您:"
    Write-Host "  1. 生成签名密钥"
    Write-Host "  2. 转换为Base64"
    Write-Host "  3. 初始化Git仓库"
    Write-Host "  4. 提交代码"
    Write-Host "  5. 配置GitHub远程仓库"
    Write-Host "  6. 推送到GitHub"
    Write-Host ""
    
    $response = Read-Host "是否继续？(Y/n)"
    
    if ($response -eq 'n' -or $response -eq 'N') {
        Print-Info "已取消操作"
        exit 0
    }
    
    # 执行步骤
    Check-Prerequisites
    Generate-Keystore
    Convert-ToBase64
    Init-GitRepo
    Commit-Files
    Setup-RemoteRepo
    Push-ToGitHub
    Show-SecretsGuide
    Show-NextSteps
}

# 运行主函数
Main
