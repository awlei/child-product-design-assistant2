#!/bin/bash

# ============================================
# 儿童产品设计助手 - 一键推送并构建脚本
# 版本: 1.0.0
# 用途: 自动推送代码到GitHub并触发构建
# ============================================

set -e  # 遇到错误立即退出

# 项目配置
GITHUB_USERNAME="awlei"
REPO_NAME="child-product-design-assistant1"
REMOTE_URL="https://github.com/awlei/child-product-design-assistant1.git"
PROJECT_NAME="儿童产品设计助手"
KEY_ALIAS="design-assistant"
KEYSTORE_FILE="release-keystore.jks"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 打印函数
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_header() {
    echo -e "\n${CYAN}============================================${NC}"
    echo -e "${CYAN}  $1${NC}"
    echo -e "${CYAN}============================================${NC}\n"
}

# 主流程
main() {
    print_header "儿童产品设计助手 - 一键推送并构建"
    
    print_info "目标仓库: ${GREEN}$REMOTE_URL${NC}"
    print_info "GitHub用户: ${GREEN}$GITHUB_USERNAME${NC}"
    echo ""
    
    # 检查是否在项目目录
    if [ ! -f "build.gradle.kts" ] && [ ! -f "app/build.gradle.kts" ]; then
        print_error "未找到项目文件，请确保在项目根目录运行此脚本"
        exit 1
    fi
    
    print_success "项目目录验证通过"
    
    # 步骤1: 初始化Git仓库
    print_header "步骤 1: 初始化Git仓库"
    
    if [ ! -d ".git" ]; then
        print_info "初始化Git仓库..."
        git init
        print_success "Git仓库初始化完成"
    else
        print_info "Git仓库已存在"
    fi
    
    # 创建.gitignore
    if [ ! -f ".gitignore" ]; then
        print_info "创建 .gitignore..."
        cat > .gitignore << 'EOF'
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
EOF
        print_success ".gitignore 创建完成"
    fi
    
    # 步骤2: 生成签名密钥
    print_header "步骤 2: 生成签名密钥"
    
    if [ ! -f "$KEYSTORE_FILE" ]; then
        print_info "生成签名密钥..."
        print_warning "使用默认密码：Keystore=YourKeystorePassword123, Key=YourKeyPassword456"
        
        keytool -genkey -v \
            -keystore "$KEYSTORE_FILE" \
            -keyalg RSA \
            -keysize 2048 \
            -validity 10000 \
            -alias "$KEY_ALIAS" \
            -dname "CN=$PROJECT_NAME, OU=Development, O=YourCompany, L=YourCity, ST=YourState, C=CN" \
            -storepass "YourKeystorePassword123" \
            -keypass "YourKeyPassword456"
        
        if [ $? -eq 0 ]; then
            print_success "签名密钥生成完成"
        else
            print_error "签名密钥生成失败"
            exit 1
        fi
    else
        print_info "签名密钥已存在，跳过生成"
    fi
    
    # 步骤3: 转换密钥为Base64
    print_header "步骤 3: 转换密钥为Base64"
    
    if [[ "$OSTYPE" == "darwin"* ]]; then
        BASE64_OUTPUT=$(base64 -i "$KEYSTORE_FILE")
    else
        BASE64_OUTPUT=$(base64 -w 0 "$KEYSTORE_FILE")
    fi
    
    echo "$BASE64_OUTPUT" > "keystore_base64.txt"
    print_success "Base64 转换完成"
    print_info "已保存到: keystore_base64.txt"
    
    # 步骤4: 添加远程仓库
    print_header "步骤 4: 配置远程仓库"
    
    if git remote get-url origin &> /dev/null; then
        print_info "更新远程仓库地址..."
        git remote set-url origin "$REMOTE_URL"
    else
        print_info "添加远程仓库..."
        git remote add origin "$REMOTE_URL"
    fi
    
    print_success "远程仓库配置完成: $REMOTE_URL"
    
    # 步骤5: 提交代码
    print_header "步骤 5: 提交代码"
    
    print_info "添加所有文件..."
    git add .
    
    print_info "创建提交..."
    git commit -m "feat: Professional Child Product Design Assistant - Initial Release

- Complete project structure for 4 product types
- Support for 5 international standards
- GPS028 design parameters
- GitHub Actions auto-build configuration
- Enhanced deployment scripts
" || print_info "没有新的更改需要提交"
    
    print_success "代码提交完成"
    
    # 步骤6: 推送到GitHub
    print_header "步骤 6: 推送到GitHub"
    
    print_warning "⚠ 需要GitHub认证"
    print_info "Username: $GITHUB_USERNAME"
    print_info "Password: 请使用您的Personal Access Token"
    echo ""
    
    # 设置main分支
    git branch -M main
    
    # 尝试推送
    print_info "正在推送代码..."
    
    if git push -u origin main; then
        print_success "✅ 代码推送成功！"
    else
        print_error "❌ 代码推送失败"
        print_info ""
        print_info "可能的原因:"
        print_info "1. GitHub仓库不存在"
        print_info "2. 认证失败（请使用Personal Access Token）"
        print_info "3. 网络问题"
        print_info ""
        print_warning "请检查后手动执行: git push -u origin main"
        exit 1
    fi
    
    # 步骤7: 显示GitHub Secrets配置指南
    print_header "步骤 7: GitHub Secrets 配置"
    
    print_info "📋 请配置以下4个GitHub Secrets:"
    echo ""
    print_success "Secret #1: KEYSTORE_BASE64"
    print_info "   值: keystore_base64.txt 文件的全部内容"
    echo ""
    print_success "Secret #2: KEYSTORE_PASSWORD"
    print_info "   值: YourKeystorePassword123"
    echo ""
    print_success "Secret #3: KEY_ALIAS"
    print_info "   值: $KEY_ALIAS"
    echo ""
    print_success "Secret #4: KEY_PASSWORD"
    print_info "   值: YourKeyPassword456"
    echo ""
    
    print_info "🔗 配置地址:"
    print_info "   https://github.com/$GITHUB_USERNAME/$REPO_NAME/settings/secrets/actions"
    echo ""
    
    # 步骤8: 显示后续步骤
    print_header "后续步骤"
    
    print_success "✅ 代码已成功推送到GitHub！"
    echo ""
    print_info "📚 接下来的操作:"
    echo ""
    echo "   1️⃣  配置GitHub Secrets（见上方）"
    echo "   2️⃣  访问Actions页面: https://github.com/$GITHUB_USERNAME/$REPO_NAME/actions"
    echo "   3️⃣  如未自动触发，点击 'Run workflow' 手动触发"
    echo "   4️⃣  等待构建完成（约5-10分钟）"
    echo "   5️⃣  在Artifacts中下载APK"
    echo ""
    print_info "🔗 快速链接:"
    echo "   • 仓库: https://github.com/$GITHUB_USERNAME/$REPO_NAME"
    echo "   • Actions: https://github.com/$GITHUB_USERNAME/$REPO_NAME/actions"
    echo "   • Secrets: https://github.com/$GITHUB_USERNAME/$REPO_NAME/settings/secrets/actions"
    echo ""
    
    # 保存快速链接
    cat > GITHUB_LINKS.txt << EOF
儿童产品设计助手 - GitHub 快速链接
===================================

仓库地址: https://github.com/$GITHUB_USERNAME/$REPO_NAME
Actions监控: https://github.com/$GITHUB_USERNAME/$REPO_NAME/actions
Secrets配置: https://github.com/$GITHUB_USERNAME/$REPO_NAME/settings/secrets/actions

配置提示:
- 确保配置了4个GitHub Secrets
- 推送代码会自动触发构建
- 构建完成后在Actions页面下载APK

生成时间: $(date)
EOF
    print_info "💾 已创建 GITHUB_LINKS.txt 保存快速链接"
    echo ""
    
    print_success "🎉 所有操作完成！"
    print_info "请立即配置GitHub Secrets以触发自动构建"
}

# 运行主函数
main
