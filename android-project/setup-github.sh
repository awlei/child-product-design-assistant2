#!/bin/bash

# ============================================
# 儿童产品设计助手 - 自动化部署脚本
# 版本: 1.0.0
# 用途: 一键初始化Git仓库、生成密钥、推送到GitHub
# ============================================

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 项目配置
PROJECT_NAME="child-product-design-assistant"
APP_NAME="儿童产品设计助手"
PACKAGE_NAME="com.design.assistant"
KEY_ALIAS="design-assistant"
KEYSTORE_FILE="release-keystore.jks"

# 打印带颜色的消息
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
    echo -e "\n${BLUE}============================================${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}============================================${NC}\n"
}

# 检查必要工具
check_prerequisites() {
    print_header "检查必要工具"
    
    local missing_tools=()
    
    if ! command -v java &> /dev/null; then
        missing_tools+=("java")
    fi
    
    if ! command -v git &> /dev/null; then
        missing_tools+=("git")
    fi
    
    if ! command -v keytool &> /dev/null; then
        missing_tools+=("keytool")
    fi
    
    if [ ${#missing_tools[@]} -ne 0 ]; then
        print_error "缺少必要工具: ${missing_tools[*]}"
        print_info "请先安装这些工具后再继续"
        exit 1
    fi
    
    print_success "所有必要工具已安装"
    java -version
    git --version
    keytool -version
}

# 生成签名密钥
generate_keystore() {
    print_header "步骤 1: 生成签名密钥"
    
    if [ -f "$KEYSTORE_FILE" ]; then
        print_warning "密钥文件已存在: $KEYSTORE_FILE"
        read -p "是否重新生成？(y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_info "使用现有密钥文件"
            return
        fi
        rm "$KEYSTORE_FILE"
    fi
    
    print_info "开始生成签名密钥..."
    print_warning "请记住您设置的密码，这些信息需要配置到GitHub Secrets中"
    
    # 生成密钥
    keytool -genkey -v \
        -keystore "$KEYSTORE_FILE" \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -alias "$KEY_ALIAS" \
        -dname "CN=$APP_NAME, OU=Development, O=YourCompany, L=YourCity, ST=YourState, C=CN" \
        -storepass "YourKeystorePassword123" \
        -keypass "YourKeyPassword456"
    
    if [ $? -eq 0 ]; then
        print_success "密钥文件生成成功: $KEYSTORE_FILE"
        print_warning "⚠ 重要提示:"
        print_warning "   - Keystore 密码: YourKeystorePassword123"
        print_warning "   - Key 密码: YourKeyPassword456"
        print_warning "   - Key 别名: $KEY_ALIAS"
        print_warning "   请将这些信息保存好，稍后需要配置到 GitHub Secrets"
    else
        print_error "密钥生成失败"
        exit 1
    fi
}

# 转换为Base64
convert_to_base64() {
    print_header "步骤 2: 转换密钥为Base64"
    
    if [ ! -f "$KEYSTORE_FILE" ]; then
        print_error "密钥文件不存在，请先生成密钥"
        exit 1
    fi
    
    print_info "正在转换密钥文件..."
    
    # 根据操作系统选择不同的base64命令
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        BASE64_OUTPUT=$(base64 -i "$KEYSTORE_FILE")
    else
        # Linux
        BASE64_OUTPUT=$(base64 -w 0 "$KEYSTORE_FILE")
    fi
    
    # 保存到文件
    echo "$BASE64_OUTPUT" > "keystore_base64.txt"
    
    print_success "Base64 转换完成"
    print_info "已保存到: keystore_base64.txt"
    
    # 显示前20个字符
    print_info "Base64 前20个字符: ${BASE64_OUTPUT:0:20}..."
}

# 初始化Git仓库
init_git_repo() {
    print_header "步骤 3: 初始化Git仓库"
    
    if [ -d ".git" ]; then
        print_warning "Git仓库已存在"
        read -p "是否重新初始化？(y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            return
        fi
        rm -rf .git
    fi
    
    print_info "初始化Git仓库..."
    git init
    print_success "Git仓库初始化完成"
    
    # 创建.gitignore
    if [ ! -f ".gitignore" ]; then
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
        print_success "已创建 .gitignore"
    fi
}

# 提交文件
commit_files() {
    print_header "步骤 4: 提交文件到Git"
    
    print_info "添加所有文件..."
    git add .
    
    print_info "创建首次提交..."
    git commit -m "Initial commit: Professional Child Product Design Assistant
    
- Complete project structure
- Core architecture for 4 product types
- 5 international standards support
- GPS028 design parameters
- GitHub Actions auto-build configuration
"
    
    print_success "文件提交完成"
}

# 配置远程仓库
setup_remote_repo() {
    print_header "步骤 5: 配置GitHub远程仓库"
    
    print_info "请提供您的GitHub用户名:"
    read -p "GitHub 用户名: " GITHUB_USERNAME
    
    if [ -z "$GITHUB_USERNAME" ]; then
        print_error "GitHub用户名不能为空"
        exit 1
    fi
    
    REMOTE_URL="https://github.com/$GITHUB_USERNAME/$PROJECT_NAME.git"
    
    print_info "远程仓库地址: $REMOTE_URL"
    print_warning "⚠ 请确保您已在GitHub上创建了仓库: $PROJECT_NAME"
    read -p "仓库已创建吗？(y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "请先在GitHub上创建仓库，然后重新运行此脚本"
        exit 1
    fi
    
    # 检查是否已存在远程仓库
    if git remote get-url origin &> /dev/null; then
        git remote set-url origin "$REMOTE_URL"
        print_success "已更新远程仓库地址"
    else
        git remote add origin "$REMOTE_URL"
        print_success "已添加远程仓库"
    fi
}

# 推送到GitHub
push_to_github() {
    print_header "步骤 6: 推送到GitHub"
    
    print_info "正在推送代码..."
    
    # 设置main分支
    git branch -M main
    
    # 尝试推送
    if git push -u origin main; then
        print_success "代码推送成功！"
    else
        print_error "推送失败"
        print_info "可能的原因:"
        print_info "1. GitHub仓库不存在"
        print_info "2. 需要GitHub身份验证"
        print_info "3. 网络问题"
        print_warning "请检查后手动执行: git push -u origin main"
    fi
}

# 显示GitHub Secrets配置指南
show_secrets_guide() {
    print_header "步骤 7: GitHub Secrets 配置指南"
    
    print_info "请按以下步骤配置GitHub Secrets:"
    echo ""
    echo "1. 访问您的GitHub仓库"
    echo "2. 点击 Settings → Secrets and variables → Actions"
    echo "3. 点击 'New repository secret'"
    echo "4. 添加以下4个密钥:"
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
    
    # 读取base64文件
    if [ -f "keystore_base64.txt" ]; then
        print_info "KEYSTORE_BASE64 的前50个字符:"
        head -c 50 keystore_base64.txt
        echo "..."
    fi
}

# 显示后续步骤
show_next_steps() {
    print_header "后续步骤"
    
    print_success "✅ 自动化部署完成！"
    echo ""
    print_info "接下来的操作:"
    echo "1. ✅ 代码已推送到GitHub"
    echo "2. 📝 配置GitHub Secrets（见上方指南）"
    echo "3. ⚙️ GitHub Actions会自动开始构建APK"
    echo "4. 📥 在Actions页面下载构建好的APK"
    echo ""
    print_info "查看构建状态:"
    echo "   https://github.com/$GITHUB_USERNAME/$PROJECT_NAME/actions"
    echo ""
    print_info "本地构建测试:"
    echo "   ./gradlew assembleDebug"
    echo ""
    print_success "祝您使用愉快！🎉"
}

# 主函数
main() {
    print_header "儿童产品设计助手 - 自动化部署"
    
    print_info "本脚本将帮助您:"
    echo "  1. 生成签名密钥"
    echo "  2. 转换为Base64"
    echo "  3. 初始化Git仓库"
    echo "  4. 提交代码"
    echo "  5. 配置GitHub远程仓库"
    echo "  6. 推送到GitHub"
    echo ""
    
    read -p "是否继续？(Y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Nn]$ ]]; then
        print_info "已取消操作"
        exit 0
    fi
    
    # 执行步骤
    check_prerequisites
    generate_keystore
    convert_to_base64
    init_git_repo
    commit_files
    setup_remote_repo
    push_to_github
    show_secrets_guide
    show_next_steps
}

# 运行主函数
main "$@"
