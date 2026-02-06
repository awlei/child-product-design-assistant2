# Android 项目部署指南

本指南将帮助您将专业儿童产品设计助手 Android 项目部署到 GitHub，并配置自动构建 APK。

---

## 步骤一：创建 GitHub 仓库

### 1.1 创建新仓库

1. 登录 [GitHub](https://github.com/)
2. 点击右上角 **+** 按钮，选择 **New repository**
3. 填写仓库信息：
   - **Repository name**: `child-product-design-assistant`
   - **Description**: 专业儿童产品设计助手 - 面向专业工程师的设计方案生成工具
   - **Public/Private**: 根据需要选择（建议选择 Private）
4. 勾选 **Initialize this repository with a README**
5. 点击 **Create repository**

---

## 步骤二：配置 GitHub Secrets（密钥）

### 2.1 生成签名密钥

在本地终端执行以下命令生成 Android 签名密钥：

```bash
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias design-assistant
```

填写以下信息：
- **Keystore password**: 设置一个强密码（记下来！）
- **Key password**: 设置一个强密码（记下来！）
- **First and Last Name**: Your Name
- **Organizational Unit**: Development
- **Organization**: Your Company
- **City or Locality**: Your City
- **State or Province**: Your Province
- **Country Code**: CN

### 2.2 将密钥转换为 Base64

```bash
# macOS/Linux
base64 -i release-keystore.jks | pbcopy

# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) | Set-Clipboard
```

### 2.3 在 GitHub 添加 Secrets

1. 进入 GitHub 仓库
2. 点击 **Settings** 标签页
3. 左侧菜单选择 **Secrets and variables** → **Actions**
4. 点击 **New repository secret**
5. 添加以下 4 个 Secrets：

| Secret Name | 说明 | 示例值 |
|-------------|------|--------|
| `KEYSTORE_BASE64` | 密钥文件的 Base64 编码 | `uQIA...（很长的字符串）` |
| `KEYSTORE_PASSWORD` | 密钥库密码 | `YourKeystorePassword` |
| `KEY_ALIAS` | 密钥别名 | `design-assistant` |
| `KEY_PASSWORD` | 密钥密码 | `YourKeyPassword` |

⚠️ **重要**：
- Base64 字符串很长，确保复制完整
- 密码强度要足够，至少 12 个字符
- 不要在任何地方明文存储这些密码

---

## 步骤三：上传项目文件

### 3.1 方法一：通过 Git 上传

```bash
# 1. 进入项目目录
cd android-project

# 2. 初始化 Git 仓库
git init

# 3. 添加所有文件
git add .

# 4. 提交更改
git commit -m "Initial commit: Professional Child Product Design Assistant"

# 5. 关联远程仓库
git remote add origin https://github.com/YOUR_USERNAME/child-product-design-assistant.git

# 6. 推送到 GitHub
git branch -M main
git push -u origin main
```

### 3.2 方法二：通过 GitHub 网页上传

1. 进入 GitHub 仓库
2. 点击 **Add file** → **Upload files**
3. 拖拽整个 `android-project` 文件夹
4. 在下方填写提交信息：
   - **Commit message**: `Initial commit: Professional Child Product Design Assistant`
5. 点击 **Commit changes**

---

## 步骤四：触发自动构建

### 4.1 自动触发

每次推送代码到 `main` 或 `develop` 分支时，会自动触发构建。

```bash
# 推送代码触发构建
git add .
git commit -m "Update feature"
git push origin main
```

### 4.2 手动触发

1. 进入 GitHub 仓库
2. 点击 **Actions** 标签页
3. 选择 **Build Release APK** 工作流
4. 点击右侧 **Run workflow** 按钮
5. 选择分支（默认 main）
6. 点击 **Run workflow**

---

## 步骤五：下载构建的 APK

### 5.1 查看构建状态

1. 进入 **Actions** 标签页
2. 查看最新的构建任务
3. 绿色 ✅ 表示成功，红色 ❌ 表示失败

### 5.2 下载 APK

1. 点击成功的构建任务
2. 滚动到页面底部的 **Artifacts** 部分
3. 点击 **app-release** 或 **app-debug** 下载 ZIP 文件
4. 解压 ZIP 文件，得到 `app-release.apk`

### 5.3 安装到设备

```bash
# 通过 ADB 安装
adb install app-release.apk

# 如果之前已安装，需要先卸载
adb uninstall com.design.assistant
adb install app-release.apk
```

---

## 步骤六：发布新版本

### 6.1 修改版本号

编辑 `app/build.gradle`：

```gradle
defaultConfig {
    applicationId "com.design.assistant"
    minSdk 24
    targetSdk 34
    versionCode 2              // 修改版本号
    versionName "1.1.0"        // 修改版本名称
    ...
}
```

### 6.2 创建 Git Tag

```bash
# 创建标签
git tag v1.1.0

# 推送标签到 GitHub
git push origin v1.1.0
```

### 6.3 创建 GitHub Release

1. 进入 **Code** 标签页
2. 点击右侧 **Releases** → **Create a new release**
3. 填写信息：
   - **Choose a tag**: `v1.1.0`
   - **Release title**: `v1.1.0 - 新功能更新`
   - **Describe this release**: 写版本更新说明
4. 点击 **Publish release**

构建完成后，APK 会自动附加到 Release 页面。

---

## 常见问题

### Q1: 构建失败，提示签名配置错误

**原因**: GitHub Secrets 配置不正确

**解决方案**:
1. 检查 `KEYSTORE_BASE64` 是否完整（应该是一行很长的字符串）
2. 确认 `KEYSTORE_PASSWORD`、`KEY_ALIAS`、`KEY_PASSWORD` 正确
3. 删除并重新创建 Secrets

### Q2: 构建成功但无法安装 APK

**原因**: 可能是签名问题或权限问题

**解决方案**:
1. 确认是 Release 版本而非 Debug 版本
2. 卸载旧版本后再安装
3. 检查设备是否允许安装未知来源应用

### Q3: Gradle 同步超时

**原因**: 网络问题或依赖下载慢

**解决方案**:
1. 使用国内镜像源（修改 `settings.gradle`）
2. 增加超时时间（在 `gradle.properties` 中）
3. 使用 Gradle 缓存（GitHub Actions 已配置）

### Q4: 多次触发构建，缓存占用空间大

**解决方案**:
1. 配置 Gradle 缓存保留策略
2. 定期清理旧的构建产物
3. 使用 Git LFS 管理大文件

---

## 性能优化建议

### 1. 减少构建时间

```yaml
# .github/workflows/build-apk.yml
- name: Cache Gradle packages
  uses: actions/cache@v3
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
```

### 2. 并行构建任务

```yaml
jobs:
  build-debug:
    runs-on: ubuntu-latest
    steps: ...
  
  build-release:
    runs-on: ubuntu-latest
    needs: build-debug  # 依赖 Debug 构建
    steps: ...
```

### 3. 仅构建变更的部分

```yaml
- name: Check file changes
  id: check-changes
  run: |
    if git diff --name-only HEAD~1 HEAD | grep -q "app/src/"; then
      echo "::set-output name=changed::true"
    fi
```

---

## 安全最佳实践

### 1. 密钥管理

- ❌ 不要在代码中硬编码密码
- ❌ 不要提交密钥文件到 Git
- ✅ 使用 GitHub Secrets 存储敏感信息
- ✅ 定期更换密钥密码

### 2. 依赖安全

```bash
# 检查依赖漏洞
./gradlew dependencyCheckAnalyze

# 更新依赖
./gradlew dependencyUpdates
```

### 3. 代码审查

- 启用分支保护规则
- 要求 PR 至少有 1 个审核
- 禁止直接推送到 main 分支

---

## 高级配置

### 1. 多渠道打包

```gradle
productFlavors {
    google {
        applicationIdSuffix ".google"
        versionNameSuffix "-google"
    }
    huawei {
        applicationIdSuffix ".huawei"
        versionNameSuffix "-huawei"
    }
}
```

### 2. 自动发布到应用商店

使用 GitHub Actions 发布到：
- Google Play Store
- 华为应用市场
- 小米应用商店
- OPPO 软件商店

### 3. 增量构建

```gradle
android {
    buildFeatures {
        buildConfig = true
    }
    
    dexOptions {
        preDexLibraries true
        maxProcessCount 8
    }
}
```

---

## 监控与日志

### 查看构建日志

1. 进入 Actions 标签页
2. 点击具体的构建任务
3. 展开每个步骤查看详细日志

### 配置通知

```yaml
- name: Send notification
  if: failure()
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

---

## 联系支持

如有问题，请：

1. 查看 [GitHub Actions 文档](https://docs.github.com/en/actions)
2. 提交 GitHub Issue
3. 联系技术支持：support@design-assistant.com

---

## 附录

### A. 完整的 Secrets 列表

| Secret Name | 必需 | 说明 |
|-------------|------|------|
| `KEYSTORE_BASE64` | ✅ | 密钥文件的 Base64 编码 |
| `KEYSTORE_PASSWORD` | ✅ | 密钥库密码 |
| `KEY_ALIAS` | ✅ | 密钥别名 |
| `KEY_PASSWORD` | ✅ | 密钥密码 |

### B. 推荐的文件忽略配置

编辑 `.gitignore`：

```gitignore
# Build files
*.apk
*.ap_
*.aab
build/
.gradle/

# Keystore files
*.jks
*.keystore

# IDE files
.idea/
*.iml
.vscode/

# OS files
.DS_Store
Thumbs.db

# Local config
local.properties
```

### C. 构建产物保留时间

| 产物 | 保留时间 | 说明 |
|------|----------|------|
| Debug APK | 7 天 | 仅用于测试 |
| Release APK | 30 天 | 用于发布 |
| 构建日志 | 90 天 | GitHub 默认 |

---

**最后更新**: 2024年1月
**维护者**: Design Assistant Team
