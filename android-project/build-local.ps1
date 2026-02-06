# ============================================
# 儿童产品设计助手 - 本地构建脚本 (Windows PowerShell)
# 版本: 1.0.0
# 用途: 快速本地构建Debug/Release APK
# ============================================

# 项目配置
$PROJECT_NAME = "儿童产品设计助手"
$DEBUG_APK = "app\build\outputs\apk\debug\app-debug.apk"
$RELEASE_APK = "app\build\outputs\apk\release\app-release-unsigned.apk"
$SIGNED_APK = "app\build\outputs\apk\release\app-release.apk"
$KEYSTORE_FILE = "release-keystore.jks"
$KEY_ALIAS = "design-assistant"

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

# 清理构建
function Clean-Build {
    Print-Header "清理构建文件"
    
    Print-Info "执行: .\gradlew.bat clean"
    $result = & .\gradlew.bat clean
    
    if ($LASTEXITCODE -eq 0) {
        Print-Success "清理完成"
    } else {
        Print-Error "清理失败"
        exit 1
    }
}

# 构建Debug APK
function Build-Debug {
    Print-Header "构建 Debug APK"
    
    Print-Info "执行: .\gradlew.bat assembleDebug"
    $result = & .\gradlew.bat assembleDebug
    
    if ($LASTEXITCODE -eq 0) {
        Print-Success "Debug APK 构建成功"
        
        if (Test-Path $DEBUG_APK) {
            $size = (Get-Item $DEBUG_APK).Length / 1MB
            Print-Success "APK 位置: $DEBUG_APK"
            Print-Success "APK 大小: $([math]::Round($size, 2)) MB"
            
            # 提供安装命令
            Print-Info "安装到设备:"
            Print-Info "  adb install -r $DEBUG_APK"
        } else {
            Print-Error "APK 文件未找到"
            exit 1
        }
    } else {
        Print-Error "Debug APK 构建失败"
        exit 1
    }
}

# 构建Release APK
function Build-Release {
    Print-Header "构建 Release APK"
    
    Print-Info "执行: .\gradlew.bat assembleRelease"
    $result = & .\gradlew.bat assembleRelease
    
    if ($LASTEXITCODE -eq 0) {
        Print-Success "Release APK 构建成功（未签名）"
        
        if (Test-Path $RELEASE_APK) {
            $size = (Get-Item $RELEASE_APK).Length / 1MB
            Print-Success "APK 位置: $RELEASE_APK"
            Print-Success "APK 大小: $([math]::Round($size, 2)) MB"
            Print-Warning "⚠ 注意: 此APK未签名，需要使用apksigner签名"
        } else {
            Print-Error "APK 文件未找到"
            exit 1
        }
    } else {
        Print-Error "Release APK 构建失败"
        exit 1
    }
}

# 构建并签名Release APK
function Build-SignedRelease {
    Print-Header "构建并签名 Release APK"
    
    # 检查密钥文件
    if (-not (Test-Path $KEYSTORE_FILE)) {
        Print-Error "未找到密钥文件: $KEYSTORE_FILE"
        Print-Info "请先运行 .\setup-github.ps1 生成密钥"
        exit 1
    }
    
    # 询问密码
    Print-Info "请输入密钥密码:"
    $KEYSTORE_PASSWORD = Read-Host -AsSecureString
    Print-Info "请输入Key密码:"
    $KEY_PASSWORD = Read-Host -AsSecureString
    
    # 转换为普通字符串
    $BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($KEYSTORE_PASSWORD)
    $KEYSTORE_PASSWORD = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)
    $BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($KEY_PASSWORD)
    $KEY_PASSWORD = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)
    
    # 构建Release APK
    Print-Info "构建 Release APK..."
    $result = & .\gradlew.bat assembleRelease
    
    if (-not (Test-Path $RELEASE_APK)) {
        Print-Error "Release APK 构建失败"
        exit 1
    }
    
    # 签名APK
    Print-Info "签名 APK..."
    $result = & apksigner sign --ks "$KEYSTORE_FILE" --ks-key-alias "$KEY_ALIAS" --ks-pass "pass:$KEYSTORE_PASSWORD" --key-pass "pass:$KEY_PASSWORD" --out "$SIGNED_APK" "$RELEASE_APK"
    
    if ($LASTEXITCODE -eq 0) {
        Print-Success "APK 签名成功"
        
        # 验证签名
        $result = & apksigner verify "$SIGNED_APK"
        if ($LASTEXITCODE -eq 0) {
            Print-Success "签名验证通过"
        } else {
            Print-Warning "签名验证失败"
        }
        
        # 对齐APK
        $result = & zipalign -v 4 "$SIGNED_APK" "${SIGNED_APK}.aligned"
        if ($LASTEXITCODE -eq 0) {
            Print-Success "APK 对齐完成"
            Move-Item "${SIGNED_APK}.aligned" "$SIGNED_APK" -Force
        }
        
        $size = (Get-Item $SIGNED_APK).Length / 1MB
        Print-Success "最终APK 位置: $SIGNED_APK"
        Print-Success "最终APK 大小: $([math]::Round($size, 2)) MB"
        Print-Info "安装到设备:"
        Print-Info "  adb install -r $SIGNED_APK"
    } else {
        Print-Error "APK 签名失败"
        exit 1
    }
}

# 运行测试
function Run-Tests {
    Print-Header "运行单元测试"
    
    Print-Info "执行: .\gradlew.bat test"
    $result = & .\gradlew.bat test
    
    if ($LASTEXITCODE -eq 0) {
        Print-Success "所有测试通过"
        
        # 显示测试报告
        Print-Info "测试报告位置:"
        Print-Info "  HTML: app\build\reports\tests\testDebugUnitTest\index.html"
    } else {
        Print-Error "测试失败"
        Print-Info "请查看测试报告获取详细信息"
        exit 1
    }
}

# 生成Lint报告
function Run-Lint {
    Print-Header "运行 Lint 检查"
    
    Print-Info "执行: .\gradlew.bat lint"
    $result = & .\gradlew.bat lint
    
    if ($LASTEXITCODE -eq 0) {
        Print-Success "Lint 检查完成"
        
        # 显示Lint报告
        if (Test-Path "app\build\reports\lint-results-debug.html") {
            Print-Info "Lint 报告: app\build\reports\lint-results-debug.html"
        }
    } else {
        Print-Error "Lint 检查发现错误"
        Print-Info "请查看Lint报告获取详细信息"
        exit 1
    }
}

# 显示菜单
function Show-Menu {
    Print-Header "儿童产品设计助手 - 本地构建工具"
    
    Write-Host "请选择操作:"
    Write-Host "  1) 清理构建文件"
    Write-Host "  2) 构建 Debug APK"
    Write-Host "  3) 构建 Release APK（未签名）"
    Write-Host "  4) 构建并签名 Release APK"
    Write-Host "  5) 运行单元测试"
    Write-Host "  6) 运行 Lint 检查"
    Write-Host "  7) 全部（清理 + 构建 Debug + 测试）"
    Write-Host "  0) 退出"
    Write-Host ""
}

# 主函数
function Main {
    Show-Menu
    $choice = Read-Host "请输入选项 [0-7]"
    
    switch ($choice) {
        '1' {
            Clean-Build
        }
        '2' {
            Build-Debug
        }
        '3' {
            Build-Release
        }
        '4' {
            Build-SignedRelease
        }
        '5' {
            Run-Tests
        }
        '6' {
            Run-Lint
        }
        '7' {
            Clean-Build
            Build-Debug
            Run-Tests
        }
        '0' {
            Print-Info "退出"
            exit 0
        }
        default {
            Print-Error "无效选项"
            exit 1
        }
    }
    
    Print-Header "操作完成"
    Print-Success "✅ 所有操作已完成"
}

# 运行主函数
Main
