# 快速配置指南 - 针对指定仓库

## 📦 项目信息

- **仓库地址**: https://github.com/awlei/child-product-design-assistant1
- **GitHub 用户名**: `awlei`
- **仓库名称**: `child-product-design-assistant1`

## 🚀 一键快速配置（推荐）

### 方式一：使用自动化脚本（Windows）

```powershell
# 1. 进入项目目录
cd path\to\android-project

# 2. 直接运行自动化脚本（已预配置仓库地址）
.\setup-github.ps1

# 3. 在提示输入GitHub用户名时，直接输入：
# awlei

# 4. 脚本会自动配置远程仓库为：
# https://github.com/awlei/child-product-design-assistant1.git
```

### 方式二：使用自动化脚本（Mac/Linux）

```bash
# 1. 进入项目目录
cd /path/to/android-project

# 2. 添加执行权限
chmod +x setup-github.sh

# 3. 运行自动化脚本
./setup-github.sh

# 4. 在提示输入GitHub用户名时，直接输入：
# awlei

# 5. 脚本会自动配置远程仓库为：
# https://github.com/awlei/child-product-design-assistant1.git
```

## ⚡ 手动快速配置（无需脚本）

如果您想手动配置，可以按照以下步骤：

### 1. 初始化Git仓库

```bash
cd android-project

# 初始化仓库
git init

# 创建 .gitignore（如果不存在）
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
# 生成密钥（使用默认密码）
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

### 3. 转换密钥为Base64

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) | Out-File keystore_base64.txt
```

**Mac/Linux:**
```bash
base64 -i release-keystore.jks > keystore_base64.txt
```

### 4. 提交并推送代码

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

# 添加远程仓库（使用您提供的仓库地址）
git remote add origin https://github.com/awlei/child-product-design-assistant1.git

# 设置main分支
git branch -M main

# 推送代码（会提示输入GitHub用户名和Personal Access Token）
git push -u origin main
```

**注意**: 如果推送时需要认证，请使用GitHub Personal Access Token代替密码。

## ⚙️ 配置GitHub Secrets

推送成功后，需要配置以下4个Secrets才能构建Release APK：

### 访问配置页面

1. 打开浏览器，访问：
   ```
   https://github.com/awlei/child-product-design-assistant1/settings/secrets/actions
   ```

2. 点击 **New repository secret**

### 添加Secrets

#### Secret #1: KEYSTORE_BASE64

- **Name**: `KEYSTORE_BASE64`
- **Value**: 打开 `keystore_base64.txt` 文件，复制全部内容
- ⚠️ **重要**: 确保复制整个文件内容，不要有遗漏

#### Secret #2: KEYSTORE_PASSWORD

- **Name**: `KEYSTORE_PASSWORD`
- **Value**: `YourKeystorePassword123`

#### Secret #3: KEY_ALIAS

- **Name**: `KEY_ALIAS`
- **Value**: `design-assistant`

#### Secret #4: KEY_PASSWORD

- **Name**: `KEY_PASSWORD`
- **Value**: `YourKeyPassword456`

### 验证配置

配置完成后，您应该看到以下4个Secrets：
```
✅ KEYSTORE_BASE64
✅ KEYSTORE_PASSWORD
✅ KEY_ALIAS
✅ KEY_PASSWORD
```

## 🎯 触发自动构建

配置完成后，有两种方式触发构建：

### 方式一：推送新代码（自动触发）

```bash
# 修改README文件触发构建
echo "" >> README.md

# 提交并推送
git add .
git commit -m "trigger: test GitHub Actions build"
git push
```

### 方式二：手动触发

1. 访问：https://github.com/awlei/child-product-design-assistant1/actions
2. 点击左侧的 **Build Release APK**
3. 点击右侧的 **Run workflow** 按钮
4. 选择 `main` 分支
5. 点击 **Run workflow**

## 📥 下载构建的APK

构建成功后（通常需要5-10分钟），下载APK：

1. 访问：https://github.com/awlei/child-product-design-assistant1/actions
2. 点击最新的工作流运行记录
3. 向下滚动到 **Artifacts** 部分
4. 点击 `app-release.apk` 下载

## 🔍 查看构建状态

实时查看构建日志：

```
https://github.com/awlei/child-product-design-assistant1/actions
```

点击正在运行的工作流，可以查看详细的构建日志。

## ❓ 常见问题

### Q1: 推送时提示 "Authentication failed"

**解决方案**:

1. 使用GitHub Personal Access Token：
   - 访问：https://github.com/settings/tokens
   - 点击 "Generate new token (classic)"
   - 选择权限：`repo` 和 `workflow`
   - 生成并复制token

2. 推送时，在密码提示处粘贴token

3. 或者使用SSH方式：
   ```bash
   git remote set-url origin git@github.com:awlei/child-product-design-assistant1.git
   ```

### Q2: 构建失败，提示 "Secrets not configured"

**解决方案**:

确保所有4个Secrets都已配置：

1. 访问：https://github.com/awlei/child-product-design-assistant1/settings/secrets/actions
2. 检查以下Secrets是否存在：
   - KEYSTORE_BASE64
   - KEYSTORE_PASSWORD
   - KEY_ALIAS
   - KEY_PASSWORD
3. 如果缺失，重新添加

### Q3: KEYSTORE_BASE64 配置错误

**解决方案**:

1. 确保从 `keystore_base64.txt` 文件复制了全部内容
2. 确保没有额外的换行或空格
3. 如果使用Windows记事本打开，注意编码格式

### Q4: 如何重新生成密钥

**解决方案**:

```bash
# 删除旧密钥
rm release-keystore.jks keystore_base64.txt

# 重新生成密钥
keytool -genkey -v \
    -keystore release-keystore.jks \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -alias design-assistant \
    -dname "CN=儿童产品设计助手, OU=Development, O=YourCompany, L=YourCity, ST=YourState, C=CN" \
    -storepass YourKeystorePassword123 \
    -keypass YourKeyPassword456

# 转换为Base64
base64 -i release-keystore.jks > keystore_base64.txt  # Mac/Linux
# 或
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) | Out-File keystore_base64.txt  # Windows

# 更新GitHub Secrets中的KEYSTORE_BASE64值
```

## 📊 项目信息速查

| 项目 | 值 |
|------|-----|
| **仓库地址** | https://github.com/awlei/child-product-design-assistant1 |
| **GitHub 用户名** | awlei |
| **仓库名称** | child-product-design-assistant1 |
| **远程仓库** | https://github.com/awlei/child-product-design-assistant1.git |
| **Actions 地址** | https://github.com/awlei/child-product-design-assistant1/actions |
| **Secrets 配置** | https://github.com/awlei/child-product-design-assistant1/settings/secrets/actions |

## ✅ 完成检查清单

配置完成后，请确认以下事项：

- [ ] 代码已成功推送到 https://github.com/awlei/child-product-design-assistant1
- [ ] 4个GitHub Secrets已全部配置
- [ ] GitHub Actions已触发构建
- [ ] 构建成功完成（无错误）
- [ ] APK已成功下载
- [ ] APK可以正常安装到设备

## 🎉 完成

恭喜！您已经成功配置了项目的GitHub仓库和自动化构建。

现在，每次推送代码到 `main` 分支时，GitHub Actions都会自动构建Release APK。

**仓库地址**: https://github.com/awlei/child-product-design-assistant1

**Actions监控**: https://github.com/awlei/child-product-design-assistant1/actions

---

需要帮助？查看完整文档：[GITHUB_GUIDE.md](./GITHUB_GUIDE.md)
