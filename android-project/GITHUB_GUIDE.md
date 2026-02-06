# GitHub 连接与自动化部署完整指南

本指南将帮助您将项目连接到 GitHub，并配置自动化构建流程。

---

## 目录

1. [准备工作](#准备工作)
2. [创建 GitHub 仓库](#创建-github-仓库)
3. [使用自动化脚本（推荐）](#使用自动化脚本推荐)
4. [手动配置（高级用户）](#手动配置高级用户)
5. [配置 GitHub Secrets](#配置-github-secrets)
6. [触发构建](#触发构建)
7. [下载 APK](#下载-apk)
8. [常见问题](#常见问题)

---

## 准备工作

在开始之前，请确保您已准备好：

- ✅ GitHub 账户（[注册链接](https://github.com/signup)）
- ✅ Git 已安装（[下载链接](https://git-scm.com/downloads)）
- ✅ 项目代码（`android-project/` 目录）
- ✅ Java JDK 17（[下载链接](https://adoptium.net/)）
- ✅ GitHub 个人访问令牌（用于认证，[获取方式](#获取-github-个人访问令牌)）

### 获取 GitHub 个人访问令牌

1. 登录 GitHub
2. 点击右上角头像 → **Settings**
3. 左侧菜单 → **Developer settings**
4. → **Personal access tokens** → **Tokens (classic)**
5. 点击 **Generate new token (classic)**
6. 填写说明（如 "Android Build"）
7. 选择权限：
   - ✅ **repo**（完整仓库访问权限）
   - ✅ **workflow**（工作流权限）
8. 点击 **Generate token**
9. **复制并保存令牌**（只显示一次，务必保存）

---

## 创建 GitHub 仓库

### 方法一：通过 GitHub 网页创建

1. 登录 [GitHub](https://github.com)
2. 点击右上角 **+** → **New repository**
3. 填写仓库信息：
   - **Repository name**: `child-product-design-assistant`
   - **Description**: `Professional Child Product Design Assistant`
   - **Public/Private**: 根据需要选择
   - **Initialize with**: 勾选 **Add a README file**
4. 点击 **Create repository**

### 方法二：通过 GitHub CLI 创建

```bash
# 安装 GitHub CLI（如果未安装）
# macOS: brew install gh
# Windows: winget install --id GitHub.cli

# 登录 GitHub
gh auth login

# 创建仓库
gh repo create child-product-design-assistant --public --description "Professional Child Product Design Assistant"

# 进入项目目录
cd android-project
```

---

## 使用自动化脚本（推荐）

我们提供了跨平台自动化脚本，可以一键完成所有配置。

### Windows 用户

1. **打开 PowerShell**（以管理员身份运行）
2. **进入项目目录**
   ```powershell
   cd path\to\android-project
   ```
3. **运行自动化脚本**
   ```powershell
   .\setup-github.ps1
   ```
4. **按照提示操作**
   - 脚本会自动检查必要工具
   - 生成签名密钥
   - 转换为 Base64
   - 初始化 Git 仓库
   - 配置远程仓库
   - 推送代码到 GitHub

### Mac/Linux 用户

1. **打开终端**
2. **进入项目目录**
   ```bash
   cd /path/to/android-project
   ```
3. **添加执行权限**
   ```bash
   chmod +x setup-github.sh
   ```
4. **运行自动化脚本**
   ```bash
   ./setup-github.sh
   ```
5. **按照提示操作**

### 脚本会完成以下操作

| 步骤 | 操作 | 说明 |
|------|------|------|
| 1 | 检查工具 | 验证 Java、Git、Keytool 是否安装 |
| 2 | 生成密钥 | 创建签名密钥 `release-keystore.jks` |
| 3 | Base64 转换 | 将密钥转换为 Base64 编码 |
| 4 | 初始化 Git | 创建 .gitignore 并初始化仓库 |
| 5 | 提交代码 | 创建首次提交 |
| 6 | 配置远程 | 设置 GitHub 远程仓库地址 |
| 7 | 推送代码 | 将代码推送到 GitHub |
| 8 | 显示指南 | 输出 GitHub Secrets 配置指南 |

### 注意事项

- ⚠️ **务必保存好密码信息**，脚本结束后会显示在屏幕上
- ⚠️ **keystore_base64.txt** 文件包含敏感信息，不要提交到 GitHub
- ⚠️ 如果推送失败，请检查 GitHub 个人访问令牌是否有效

---

## 手动配置（高级用户）

如果您熟悉 Git 和 GitHub 操作，可以手动配置。

### 1. 初始化 Git 仓库

```bash
cd android-project

# 初始化仓库
git init

# 创建 .gitignore
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
```

### 2. 生成签名密钥

```bash
# 生成密钥
keytool -genkey -v \
    -keystore release-keystore.jks \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -alias design-assistant \
    -dname "CN=儿童产品设计助手, OU=Development, O=YourCompany, L=YourCity, ST=YourState, C=CN" \
    -storepass YourKeystorePassword123 \
    -keypass YourKeyPassword456
```

### 3. 转换密钥为 Base64

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) | Out-File keystore_base64.txt
```

**Mac/Linux:**
```bash
base64 -i release-keystore.jks > keystore_base64.txt
```

### 4. 提交代码

```bash
# 添加所有文件
git add .

# 创建提交
git commit -m "Initial commit: Professional Child Product Design Assistant

- Complete project structure
- Core architecture for 4 product types
- 5 international standards support
- GPS028 design parameters
- GitHub Actions auto-build configuration
"

# 设置远程仓库（替换为您的仓库地址）
git remote add origin https://github.com/YOUR_USERNAME/child-product-design-assistant.git

# 推送代码（可能需要输入 GitHub 用户名和 Personal Access Token）
git branch -M main
git push -u origin main
```

---

## 配置 GitHub Secrets

在代码推送成功后，需要配置 Secrets 才能构建 Release APK。

### 步骤

1. **进入仓库的 Settings 页面**
   - 访问：`https://github.com/YOUR_USERNAME/child-product-design-assistant/settings`

2. **进入 Secrets 配置**
   - 左侧菜单 → **Secrets and variables** → **Actions**

3. **添加 Secrets**
   - 点击 **New repository secret**
   - 依次添加以下4个 Secrets

### Secret #1: KEYSTORE_BASE64

- **Name**: `KEYSTORE_BASE64`
- **Value**: 打开 `keystore_base64.txt` 文件，复制全部内容
- ⚠️ **注意**: 不要有换行或空格

**如何获取:**
- 运行自动化脚本后，文件位于项目根目录
- 使用文本编辑器打开，复制所有内容

### Secret #2: KEYSTORE_PASSWORD

- **Name**: `KEYSTORE_PASSWORD`
- **Value**: `YourKeystorePassword123`

### Secret #3: KEY_ALIAS

- **Name**: `KEY_ALIAS`
- **Value**: `design-assistant`

### Secret #4: KEY_PASSWORD

- **Name**: `KEY_PASSWORD`
- **Value**: `YourKeyPassword456`

### 验证配置

配置完成后，您应该看到以下 Secrets：
```
✅ KEYSTORE_BASE64
✅ KEYSTORE_PASSWORD
✅ KEY_ALIAS
✅ KEY_PASSWORD
```

---

## 触发构建

### 自动触发

配置完 Secrets 后，有两种方式可以触发构建：

#### 方式一：推送新代码（推荐）

```bash
# 修改代码
echo "// test change" >> README.md

# 提交并推送
git add .
git commit -m "test: trigger build"
git push
```

推送成功后，GitHub Actions 会自动开始构建。

#### 方式二：手动触发

1. 进入 **Actions** 标签页
2. 选择 **Build Release APK** 工作流
3. 点击右侧 **Run workflow** 按钮
4. 选择分支（通常是 `main`）
5. 点击绿色的 **Run workflow** 按钮

### 查看构建状态

1. 进入 **Actions** 标签页
2. 可以看到所有工作流运行记录
3. 点击最新的运行记录查看详细日志

**构建时间**: 通常需要 **5-10 分钟**

### 构建流程

GitHub Actions 会执行以下步骤：

| 步骤 | 操作 | 耗时 |
|------|------|------|
| 1 | 检出代码 | 10秒 |
| 2 | 设置 JDK 17 | 30秒 |
| 3 | 缓存 Gradle | 20秒（首次需要2-3分钟） |
| 4 | 构建 Release APK | 3-5分钟 |
| 5 | 签名 APK | 10秒 |
| 6 | 上传 Artifact | 30秒 |

---

## 下载 APK

### 下载位置

构建成功后，APK 会作为 Artifact 上传：

1. 进入 **Actions** 标签页
2. 点击最新的 **Build Release APK** 运行记录
3. 向下滚动到 **Artifacts** 部分
4. 点击 `app-release.apk` 下载

### Artifact 保留期

- **免费账户**: 90 天
- **Pro 账户**: 永久保留

### 验证 APK

下载后，您可以使用以下命令验证 APK：

```bash
# 查看 APK 信息
aapt dump badging app-release.apk

# 验证签名
apksigner verify app-release.apk

# 安装到设备
adb install app-release.apk
```

---

## 常见问题

### Q1: 推送代码时提示 "Authentication failed"

**原因**: GitHub 认证失败

**解决方案**:
1. 使用 GitHub Personal Access Token 代替密码
2. 确保令牌具有 `repo` 和 `workflow` 权限
3. 尝试使用 SSH 而非 HTTPS

### Q2: GitHub Actions 构建失败

**常见原因**:

1. **Secrets 未配置或配置错误**
   - 检查所有 4 个 Secrets 是否都已配置
   - 验证 `KEYSTORE_BASE64` 的值是否正确

2. **签名密钥无效**
   - 重新生成密钥
   - 确保密码正确

3. **构建错误**
   - 查看 Actions 日志中的错误信息
   - 检查代码是否有编译错误

### Q3: APK 无法安装

**原因**: 签名问题或包名冲突

**解决方案**:
1. 卸载旧版本: `adb uninstall com.design.assistant`
2. 验证签名: `apksigner verify app-release.apk`
3. 使用 `-r` 参数重新安装: `adb install -r app-release.apk`

### Q4: 如何修改包名？

1. 编辑 `app/build.gradle.kts` 中的 `namespace`
2. 修改所有源文件中的 `package` 声明
3. 重新生成密钥（使用新的包名）
4. 更新 GitHub Secrets

### Q5: 如何加快构建速度？

1. **启用 Gradle 缓存**: 已在 GitHub Actions 中配置
2. **减少依赖**: 移除不必要的库
3. **使用自定义 Runner**: 使用 GitHub Enterprise 或自托管 Runner

---

## 下一步

配置完成后，您可以：

1. 🎨 **开发新功能**: 按照 [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) 添加新功能
2. 🐛 **修复 Bug**: 提交代码并自动触发构建
3. 📝 **更新文档**: 完善 README 和 API 文档
4. 🚀 **发布新版本**: 使用 GitHub Releases 发布正式版本

---

## 技术支持

如果遇到问题，可以：

1. 查看 [GitHub Actions 文档](https://docs.github.com/en/actions)
2. 检查 [项目 Issues](../../issues)
3. 联系开发团队

---

**祝您使用愉快！🎉**
