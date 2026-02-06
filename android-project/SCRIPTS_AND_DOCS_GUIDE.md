# 脚本和文档使用指南

本指南帮助您选择合适的脚本和文档来完成不同的任务。

---

## 📋 脚本列表

### 🚀 自动化部署脚本

#### 1. setup-github-enhanced.sh / setup-github-enhanced.ps1 ⭐ 推荐

**用途**: 一键完成所有配置，支持预配置仓库

**适用场景**:
- 连接到预配置的GitHub仓库（https://github.com/awlei/child-product-design-assistant1）
- 首次部署，需要完整配置
- 希望自动化完成所有步骤

**功能**:
- ✅ 自动检测预配置仓库信息
- ✅ 生成签名密钥
- ✅ 转换为Base64
- ✅ 初始化Git仓库
- ✅ 配置远程仓库
- ✅ 推送代码到GitHub
- ✅ 生成GitHub Secrets配置指南
- ✅ 创建快速链接文件（GITHUB_LINKS.txt）

**使用方法**:

Windows:
```powershell
.\setup-github-enhanced.ps1
```

Mac/Linux:
```bash
chmod +x setup-github-enhanced.sh
./setup-github-enhanced.sh
```

---

#### 2. setup-github.sh / setup-github.ps1

**用途**: 标准自动化部署脚本

**适用场景**:
- 连接到自定义GitHub仓库
- 需要手动输入仓库信息
- 标准部署流程

**功能**:
- ✅ 生成签名密钥
- ✅ 转换为Base64
- ✅ 初始化Git仓库
- ✅ 配置远程仓库
- ✅ 推送代码到GitHub
- ✅ 生成GitHub Secrets配置指南

**使用方法**:

Windows:
```powershell
.\setup-github.ps1
```

Mac/Linux:
```bash
chmod +x setup-github.sh
./setup-github.sh
```

---

### 🛠️ 本地构建脚本

#### 3. build-local.sh / build-local.ps1

**用途**: 本地构建Debug/Release APK

**适用场景**:
- 在本地测试构建
- 不想推送代码到GitHub
- 快速生成APK进行测试

**功能**:
- ✅ 清理构建文件
- ✅ 构建Debug APK
- ✅ 构建Release APK（未签名）
- ✅ 构建并签名Release APK
- ✅ 运行单元测试
- ✅ 运行Lint检查

**使用方法**:

Windows:
```powershell
.\build-local.ps1
```

Mac/Linux:
```bash
chmod +x build-local.sh
./build-local.sh
```

---

## 📚 文档列表

### 🎯 快速开始指南

#### 1. QUICK_SETUP_SPECIFIC_REPO.md

**用途**: 针对指定仓库的快速配置指南

**适用场景**:
- 连接到 https://github.com/awlei/child-product-design-assistant1
- 需要快速配置步骤
- 遇到特定仓库的配置问题

**内容**:
- 📦 项目信息
- 🚀 一键快速配置步骤
- ⚡ 手动快速配置步骤
- ⚙️ GitHub Secrets配置
- 🎯 触发自动构建
- 📥 下载构建的APK
- ❓ 常见问题解答

---

#### 2. QUICK_START.md

**用途**: 5分钟快速上手指南

**适用场景**:
- 新手首次使用
- 需要快速了解项目
- 了解基本操作流程

**内容**:
- 项目简介
- 基础概念
- 核心功能演示
- 常见操作

---

### 📖 完整指南

#### 3. GITHUB_GUIDE.md

**用途**: GitHub连接与自动化部署完整指南

**适用场景**:
- 需要详细的部署步骤
- 遇到GitHub配置问题
- 想要了解完整的部署流程

**内容**:
- 准备工作
- 创建GitHub仓库
- 使用自动化脚本
- 手动配置（高级用户）
- 配置GitHub Secrets
- 触发构建
- 下载APK
- 常见问题

---

#### 4. DEPLOYMENT_GUIDE.md

**用途**: 部署与维护指南

**适用场景**:
- 生产环境部署
- 系统维护
- 版本更新

**内容**:
- 部署流程
- 环境配置
- 性能优化
- 监控和日志
- 备份和恢复

---

#### 5. README.md

**用途**: 项目主文档

**适用场景**:
- 了解项目概述
- 查看核心功能
- 查看文档导航

**内容**:
- 项目概述
- 核心功能
- 快速开始
- GitHub Actions配置
- 项目结构

---

### 📋 检查清单

#### 6. FILE_CHECKLIST.md

**用途**: 项目文件完整列表

**适用场景**:
- 验证项目完整性
- 查找特定文件
- 了解项目结构

**内容**:
- 所有文件清单
- 文件说明
- 目录结构

---

#### 7. VERIFICATION_CHECKLIST.md

**用途**: 项目验证清单

**适用场景**:
- 验证配置是否正确
- 检查构建环境
- 确认部署状态

**内容**:
- 项目文件清单
- 功能模块验证
- 构建验证
- 快速开始检查清单

---

#### 8. DELIVERY_SUMMARY.md

**用途**: 项目交付说明

**适用场景**:
- 项目交付
- 了解交付内容
- 后续开发参考

**内容**:
- 交付内容
- 项目结构
- 技术栈
- 后续开发指南

---

## 🎯 使用场景推荐

### 场景1: 首次部署到指定仓库

**推荐流程**:

1. 阅读 `QUICK_SETUP_SPECIFIC_REPO.md`
2. 运行 `setup-github-enhanced.sh` / `setup-github-enhanced.ps1`
3. 配置GitHub Secrets
4. 触发自动构建

**预计时间**: 10-15分钟

---

### 场景2: 首次部署到自定义仓库

**推荐流程**:

1. 阅读 `GITHUB_GUIDE.md`
2. 运行 `setup-github.sh` / `setup-github.ps1`
3. 配置GitHub Secrets
4. 触发自动构建

**预计时间**: 15-20分钟

---

### 场景3: 本地测试构建

**推荐流程**:

1. 运行 `build-local.sh` / `build-local.ps1`
2. 选择构建选项
3. 在本地安装测试

**预计时间**: 5-10分钟

---

### 场景4: 遇到问题

**推荐流程**:

1. 查看 `QUICK_SETUP_SPECIFIC_REPO.md` 的常见问题部分
2. 查看 `GITHUB_GUIDE.md` 的常见问题部分
3. 检查 `VERIFICATION_CHECKLIST.md`

---

### 场景5: 了解项目结构

**推荐流程**:

1. 阅读 `README.md`
2. 查看 `FILE_CHECKLIST.md`
3. 查看 `DELIVERY_SUMMARY.md`

---

## 🔍 快速查找

| 我想要... | 使用... |
|-----------|---------|
| 快速连接到指定仓库 | `QUICK_SETUP_SPECIFIC_REPO.md` + `setup-github-enhanced.sh/ps1` |
| 快速连接到自定义仓库 | `GITHUB_GUIDE.md` + `setup-github.sh/ps1` |
| 本地构建APK | `build-local.sh/ps1` |
| 了解项目概述 | `README.md` |
| 查看所有文件 | `FILE_CHECKLIST.md` |
| 验证配置 | `VERIFICATION_CHECKLIST.md` |
| 解决问题 | `GITHUB_GUIDE.md` 的常见问题部分 |
| 5分钟上手 | `QUICK_START.md` |

---

## 💡 提示

1. **首次使用**: 建议从 `QUICK_SETUP_SPECIFIC_REPO.md` 开始
2. **遇到问题**: 优先查看对应文档的"常见问题"部分
3. **自动化优先**: 优先使用自动化脚本，避免手动操作错误
4. **保存信息**: 运行脚本后，记得保存生成的密码和密钥信息
5. **参考文档**: 遇到不确定的问题，参考完整指南文档

---

## 📞 技术支持

如果文档和脚本无法解决您的问题：

1. 检查所有相关文档的"常见问题"部分
2. 查看GitHub Actions构建日志
3. 检查项目Issues（如果有）

---

**最后更新**: 2025-01-21
