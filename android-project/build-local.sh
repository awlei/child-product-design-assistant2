#!/bin/bash

# ============================================
# 儿童产品设计助手 - 本地构建脚本
# 版本: 1.0.0
# 用途: 快速本地构建Debug/Release APK
# ============================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 项目配置
PROJECT_NAME="儿童产品设计助手"
DEBUG_APK="app/build/outputs/apk/debug/app-debug.apk"
RELEASE_APK="app/build/outputs/apk/release/app-release-unsigned.apk"
SIGNED_APK="app/build/outputs/apk/release/app-release.apk"
KEYSTORE_FILE="release-keystore.jks"
KEY_ALIAS="design-assistant"

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

# 清理构建
clean_build() {
    print_header "清理构建文件"
    
    print_info "执行: ./gradlew clean"
    if ./gradlew clean; then
        print_success "清理完成"
    else
        print_error "清理失败"
        exit 1
    fi
}

# 构建Debug APK
build_debug() {
    print_header "构建 Debug APK"
    
    print_info "执行: ./gradlew assembleDebug"
    if ./gradlew assembleDebug; then
        print_success "Debug APK 构建成功"
        
        if [ -f "$DEBUG_APK" ]; then
            local size=$(du -h "$DEBUG_APK" | cut -f1)
            print_success "APK 位置: $DEBUG_APK"
            print_success "APK 大小: $size"
            
            # 提供安装命令
            print_info "安装到设备:"
            print_info "  adb install -r $DEBUG_APK"
        else
            print_error "APK 文件未找到"
            exit 1
        fi
    else
        print_error "Debug APK 构建失败"
        exit 1
    fi
}

# 构建Release APK
build_release() {
    print_header "构建 Release APK"
    
    print_info "执行: ./gradlew assembleRelease"
    if ./gradlew assembleRelease; then
        print_success "Release APK 构建成功（未签名）"
        
        if [ -f "$RELEASE_APK" ]; then
            local size=$(du -h "$RELEASE_APK" | cut -f1)
            print_success "APK 位置: $RELEASE_APK"
            print_success "APK 大小: $size"
            print_warning "⚠ 注意: 此APK未签名，需要使用apksigner签名"
        else
            print_error "APK 文件未找到"
            exit 1
        fi
    else
        print_error "Release APK 构建失败"
        exit 1
    fi
}

# 构建并签名Release APK
build_signed_release() {
    print_header "构建并签名 Release APK"
    
    # 检查密钥文件
    if [ ! -f "$KEYSTORE_FILE" ]; then
        print_error "未找到密钥文件: $KEYSTORE_FILE"
        print_info "请先运行 ./setup-github.sh 生成密钥"
        exit 1
    fi
    
    # 询问密码
    print_info "请输入密钥密码:"
    read -s KEYSTORE_PASSWORD
    echo
    read -s KEY_PASSWORD
    echo
    
    # 构建Release APK
    print_info "构建 Release APK..."
    ./gradlew assembleRelease
    
    if [ ! -f "$RELEASE_APK" ]; then
        print_error "Release APK 构建失败"
        exit 1
    fi
    
    # 签名APK
    print_info "签名 APK..."
    if apksigner sign --ks "$KEYSTORE_FILE" --ks-key-alias "$KEY_ALIAS" --ks-pass "pass:$KEYSTORE_PASSWORD" --key-pass "pass:$KEY_PASSWORD" --out "$SIGNED_APK" "$RELEASE_APK"; then
        print_success "APK 签名成功"
        
        # 验证签名
        if apksigner verify "$SIGNED_APK"; then
            print_success "签名验证通过"
        else
            print_warning "签名验证失败"
        fi
        
        # 对齐APK
        if zipalign -v 4 "$SIGNED_APK" "${SIGNED_APK}.aligned"; then
            print_success "APK 对齐完成"
            mv "${SIGNED_APK}.aligned" "$SIGNED_APK"
        fi
        
        local size=$(du -h "$SIGNED_APK" | cut -f1)
        print_success "最终APK 位置: $SIGNED_APK"
        print_success "最终APK 大小: $size"
        print_info "安装到设备:"
        print_info "  adb install -r $SIGNED_APK"
    else
        print_error "APK 签名失败"
        exit 1
    fi
}

# 运行测试
run_tests() {
    print_header "运行单元测试"
    
    print_info "执行: ./gradlew test"
    if ./gradlew test; then
        print_success "所有测试通过"
        
        # 显示测试报告
        print_info "测试报告位置:"
        print_info "  HTML: app/build/reports/tests/testDebugUnitTest/index.html"
    else
        print_error "测试失败"
        print_info "请查看测试报告获取详细信息"
        exit 1
    fi
}

# 生成Lint报告
run_lint() {
    print_header "运行 Lint 检查"
    
    print_info "执行: ./gradlew lint"
    if ./gradlew lint; then
        print_success "Lint 检查完成"
        
        # 显示Lint报告
        if [ -f "app/build/reports/lint-results-debug.html" ]; then
            print_info "Lint 报告: app/build/reports/lint-results-debug.html"
        fi
    else
        print_error "Lint 检查发现错误"
        print_info "请查看Lint报告获取详细信息"
        exit 1
    fi
}

# 显示菜单
show_menu() {
    print_header "儿童产品设计助手 - 本地构建工具"
    
    echo "请选择操作:"
    echo "  1) 清理构建文件"
    echo "  2) 构建 Debug APK"
    echo "  3) 构建 Release APK（未签名）"
    echo "  4) 构建并签名 Release APK"
    echo "  5) 运行单元测试"
    echo "  6) 运行 Lint 检查"
    echo "  7) 全部（清理 + 构建 Debug + 测试）"
    echo "  0) 退出"
    echo ""
}

# 主函数
main() {
    show_menu
    read -p "请输入选项 [0-7]: " choice
    
    case $choice in
        1)
            clean_build
            ;;
        2)
            build_debug
            ;;
        3)
            build_release
            ;;
        4)
            build_signed_release
            ;;
        5)
            run_tests
            ;;
        6)
            run_lint
            ;;
        7)
            clean_build
            build_debug
            run_tests
            ;;
        0)
            print_info "退出"
            exit 0
            ;;
        *)
            print_error "无效选项"
            exit 1
            ;;
    esac
    
    print_header "操作完成"
    print_success "✅ 所有操作已完成"
}

# 运行主函数
main "$@"
