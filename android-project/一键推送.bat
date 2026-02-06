@echo off
chcp 65001 >nul
echo ============================================
echo   儿童产品设计助手 - 一键推送并构建
echo ============================================
echo.

cd /d "%~dp0"

echo [1/8] 检查项目目录...
if not exist "build.gradle.kts" if not exist "app\build.gradle.kts" (
    echo [错误] 未找到项目文件，请确保在项目根目录运行此脚本
    pause
    exit /b 1
)
echo [√] 项目目录验证通过
echo.

echo [2/8] 初始化Git仓库...
if not exist ".git" (
    git init
)
echo [√] Git仓库已就绪
echo.

echo [3/8] 创建.gitignore...
if not exist ".gitignore" (
    (
        echo # Build files
        echo *.apk *.ap_ *.aab build/ .gradle/
        echo.
        echo # Keystore files
        echo *.jks *.keystore keystore_base64.txt keystore-passwords.txt
        echo.
        echo # IDE files
        echo .idea/ *.iml .vscode/
        echo.
        echo # OS files
        echo .DS_Store Thumbs.db
        echo.
        echo # Local config
        echo local.properties
    ) > .gitignore
)
echo [√] .gitignore 已创建
echo.

echo [4/8] 生成签名密钥...
if not exist "release-keystore.jks" (
    keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias design-assistant -dname "CN=儿童产品设计助手, OU=Development, O=YourCompany, L=YourCity, ST=YourState, C=CN" -storepass YourKeystorePassword123 -keypass YourKeyPassword456
)
echo [√] 签名密钥已就绪
echo.

echo [5/8] 转换密钥为Base64...
certutil -encode release-keystore.jks keystore_base64_temp.txt >nul 2>&1
powershell -Command "(Get-Content keystore_base64_temp.txt) -join '' | Set-Content keystore_base64.txt"
del keystore_base64_temp.txt
echo [√] Base64 转换完成
echo.

echo [6/8] 配置远程仓库...
git remote get-url origin >nul 2>&1
if %errorlevel% equ 0 (
    git remote set-url origin https://github.com/awlei/child-product-design-assistant1.git
) else (
    git remote add origin https://github.com/awlei/child-product-design-assistant1.git
)
echo [√] 远程仓库已配置
echo.

echo [7/8] 提交代码...
git add .
git commit -m "feat: Professional Child Product Design Assistant - Initial Release" 2>nul
echo [√] 代码已提交
echo.

echo [8/8] 推送到GitHub...
echo.
echo ============================================
echo   ⚠ 需要GitHub认证
echo ============================================
echo.
echo Username: awlei
echo Password: 请粘贴您的Personal Access Token
echo.
echo 提示: 如果密码认证失败，请使用Personal Access Token
echo 获取Token: https://github.com/settings/tokens
echo.
echo ============================================
echo.

git branch -M main
git push -u origin main

if %errorlevel% equ 0 (
    echo.
    echo ============================================
    echo   ✅ 代码推送成功！
    echo ============================================
    echo.
    echo 📋 接下来的操作:
    echo.
    echo 1. 配置GitHub Secrets
    echo    访问: https://github.com/awlei/child-product-design-assistant1/settings/secrets/actions
    echo.
    echo 2. 添加以下4个Secrets:
    echo    KEYSTORE_BASE64    = keystore_base64.txt 的全部内容
    echo    KEYSTORE_PASSWORD  = YourKeystorePassword123
    echo    KEY_ALIAS          = design-assistant
    echo    KEY_PASSWORD       = YourKeyPassword456
    echo.
    echo 3. 触发构建
    echo    访问: https://github.com/awlei/child-product-design-assistant1/actions
    echo.
    echo 4. 下载APK
    echo    构建完成后在Artifacts中下载
    echo.
) else (
    echo.
    echo ============================================
    echo   ❌ 推送失败
    echo ============================================
    echo.
    echo 可能的原因:
    echo 1. GitHub仓库不存在
    echo 2. 认证失败（请使用Personal Access Token）
    echo 3. 网络问题
    echo.
    echo 请检查后重试
    echo.
)

pause
