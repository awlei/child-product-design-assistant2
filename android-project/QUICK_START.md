# 快速开始指南

本指南帮助您在 10 分钟内完成项目的基本设置和首次构建。

---

## 第一步：准备环境（5 分钟）

### 1.1 安装必要工具

确保您的电脑已安装：

| 工具 | 版本要求 | 下载地址 |
|------|----------|----------|
| Android Studio | Koala (2024.1.1) 或更高 | [下载](https://developer.android.com/studio) |
| JDK | 17 | 包含在 Android Studio 中 |
| Git | 任意版本 | [下载](https://git-scm.com/downloads) |

### 1.2 验证环境

打开终端，执行以下命令：

```bash
java -version
# 输出：openjdk version "17.x.x"

git --version
# 输出：git version 2.x.x

adb version
# 输出：Android Debug Bridge version x.x.x
```

---

## 第二步：创建 GitHub 仓库（2 分钟）

### 2.1 创建仓库

1. 访问 [GitHub New Repository](https://github.com/new)
2. 填写：
   - Repository name: `child-product-design-assistant`
   - Description: 专业儿童产品设计助手
   - 勾选 "Add a README file"
3. 点击 "Create repository"

### 2.2 生成签名密钥

```bash
# 在任意目录执行
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias design-assistant

# 设置密码（记住这些密码！）
# Keystore password: YourPassword123
# Key password: YourPassword456
```

### 2.3 转换为 Base64

```bash
# macOS/Linux
base64 -i release-keystore.jks | pbcopy

# Windows
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) | Set-Clipboard
```

### 2.4 配置 GitHub Secrets

1. 进入仓库 Settings → Secrets and variables → Actions
2. 点击 "New repository secret"，添加以下 4 个密钥：

| Name | Value |
|------|-------|
| `KEYSTORE_BASE64` | 粘贴 Base64 字符串（很长） |
| `KEYSTORE_PASSWORD` | `YourPassword123` |
| `KEY_ALIAS` | `design-assistant` |
| `KEY_PASSWORD` | `YourPassword456` |

---

## 第三步：上传项目（3 分钟）

### 3.1 使用 Git 上传

```bash
# 1. 进入项目目录
cd android-project

# 2. 初始化 Git
git init

# 3. 添加远程仓库
git remote add origin https://github.com/YOUR_USERNAME/child-product-design-assistant.git

# 4. 提交文件
git add .
git commit -m "Initial commit"

# 5. 推送
git branch -M main
git push -u origin main
```

### 3.2 验证上传

1. 访问 GitHub 仓库
2. 查看文件列表，确保所有文件都已上传
3. 进入 "Actions" 标签页
4. 看到 "Build Release APK" 工作流自动开始运行 ✅

---

## 第四步：下载首次构建的 APK（等待 5-10 分钟）

### 4.1 查看构建状态

1. 进入 "Actions" 标签页
2. 点击正在运行的任务
3. 查看构建进度

### 4.2 下载 APK

1. 构建完成后，刷新页面
2. 点击 "app-release" 或 "app-debug"
3. 下载 ZIP 文件并解压
4. 得到 `app-release.apk` 或 `app-debug.apk`

### 4.3 安装测试

```bash
# 通过 ADB 安装
adb install app-debug.apk

# 或直接将 APK 文件发送到手机安装
```

---

## 常见问题快速排查

### ❌ 问题1：构建失败，提示签名错误

**解决方案**：
1. 检查 GitHub Secrets 是否正确配置
2. 确认 Base64 字符串完整（应该是一行，没有换行）
3. 删除并重新添加 Secrets

### ❌ 问题2：Gradle 同步失败

**解决方案**：
```bash
# 清理缓存
./gradlew clean

# 重新同步
# 在 Android Studio 中：File → Sync Project with Gradle Files
```

### ❌ 问题3：无法安装 APK

**解决方案**：
1. 确认手机已开启"开发者选项"
2. 允许"USB 调试"和"安装未知应用"
3. 使用 `adb install` 命令安装

---

## 下一步

现在您已经完成了基本设置，可以：

1. ✅ **继续开发**：参考 `FILE_CHECKLIST.md` 创建剩余的代码文件
2. ✅ **自定义配置**：修改 `strings.xml`、`colors.xml` 等资源文件
3. ✅ **添加功能**：按照架构设计添加新产品或新标准

---

## 需要帮助？

- 📖 详细文档：查看 `README.md` 和 `DEPLOYMENT_GUIDE.md`
- 🔧 构建问题：查看 `FILE_CHECKLIST.md`
- 💬 技术支持：提交 GitHub Issue

---

**预计完成时间**: 10-15 分钟
**难度**: ⭐⭐☆☆☆ (初级)
