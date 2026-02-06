# 专业儿童产品设计助手 - Android 原生项目完整指南

## 项目概述

这是一个面向专业儿童产品设计工程师的 Android 应用，提供基于全球标准（ECE R129、GB 27887-2024、FMVSS 213、AS/NZS 1754、CMVSS 213）的儿童产品设计方案生成工具。

### 核心功能

- ✅ **四大品类支持**：儿童安全座椅、婴儿推车、儿童高脚椅、儿童床
- ✅ **五大标准体系**：欧盟、中国、美国、澳洲、加拿大
- ✅ **GPS028设计参数**：头枕高度、座宽、Envelop尺寸、侧防面积
- ✅ **专业级输出**：标准→基础数据→设计参数→测试要求→测试项
- ✅ **多标准兼容建议**：自动生成跨标准设计优化建议

### 📚 文档导航

- [🎯 针对指定仓库的快速配置](./QUICK_SETUP_SPECIFIC_REPO.md) - 快速连接到指定GitHub仓库
- [📖 脚本和文档使用指南](./SCRIPTS_AND_DOCS_GUIDE.md) - 了解所有脚本和文档的使用方法
- [🚀 GitHub 连接与自动化部署完整指南](./GITHUB_GUIDE.md) - 一键连接GitHub并配置自动构建
- [⚙️ 部署与维护指南](./DEPLOYMENT_GUIDE.md) - 生产环境部署指南
- [⚡ 快速开始指南](./QUICK_START.md) - 5分钟上手
- [📋 文件清单](./FILE_CHECKLIST.md) - 项目文件完整列表
- [✅ 项目验证清单](./VERIFICATION_CHECKLIST.md) - 验证项目配置
- [📦 交付总结](./DELIVERY_SUMMARY.md) - 项目交付说明

### 🌐 项目链接

- **GitHub 仓库**: https://github.com/awlei/child-product-design-assistant1
- **Actions 监控**: https://github.com/awlei/child-product-design-assistant1/actions
- **Secrets 配置**: https://github.com/awlei/child-product-design-assistant1/settings/secrets/actions

---

## 快速开始

### 前置要求

- Android Studio Koala (2024.1.1) 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.0
- Git
- GitHub 账户（用于自动构建）

### 🚀 一键快速开始（推荐）

#### 方式一：使用增强版自动化脚本（指定仓库）

如果您要连接到预配置的GitHub仓库（https://github.com/awlei/child-product-design-assistant1），请使用增强版脚本：

##### Windows 用户
```powershell
# 在项目根目录打开 PowerShell
.\setup-github-enhanced.ps1
```

##### Mac/Linux 用户
```bash
# 在项目根目录打开终端
chmod +x setup-github-enhanced.sh
./setup-github-enhanced.sh
```

增强版脚本特性：
- ✅ 预配置仓库信息（自动识别 https://github.com/awlei/child-product-design-assistant1）
- ✅ 一键完成所有配置
- ✅ 自动生成快速链接文件（GITHUB_LINKS.txt）
- ✅ 更友好的用户提示和错误处理

#### 方式二：使用标准自动化脚本

我们提供了标准自动化脚本，让您能够：

1. **一键生成签名密钥**
2. **自动初始化Git仓库**
3. **配置GitHub远程仓库**
4. **推送代码到GitHub**
5. **获取GitHub Secrets配置指南**

##### Windows 用户
```powershell
# 在项目根目录打开 PowerShell
.\setup-github.ps1
```

##### Mac/Linux 用户
```bash
# 在项目根目录打开终端
chmod +x setup-github.sh
./setup-github.sh
```

脚本会引导您完成所有步骤，最后提供详细的GitHub Secrets配置指南。

#### 方式三：使用本地构建脚本

如果您只想在本地构建APK，可以使用以下脚本：

##### Windows 用户
```powershell
# 在项目根目录打开 PowerShell
.\build-local.ps1
```

##### Mac/Linux 用户
```bash
# 在项目根目录打开终端
chmod +x build-local.sh
./build-local.sh
```

脚本提供交互式菜单，支持以下操作：
- 清理构建文件
- 构建 Debug APK
- 构建 Release APK（未签名）
- 构建并签名 Release APK
- 运行单元测试
- 运行 Lint 检查
- 全部（清理 + 构建 Debug + 测试）

### 手动构建步骤

如果您更喜欢手动操作，可以按照以下步骤：

1. **克隆项目**
```bash
git clone <your-repo-url>
cd child-product-design-assistant
```

2. **使用 Android Studio 打开项目**
```
File -> Open -> 选择项目根目录
```

3. **同步 Gradle**
```
等待 Gradle 同步完成（首次需要下载依赖）
```

4. **连接设备或启动模拟器**
```
确保已连接 Android 设备或启动模拟器
```

5. **运行应用**
```
点击 Run 按钮 (▶️) 或使用快捷键 Shift + F10
```

6. **生成 Debug APK**
```bash
./gradlew assembleDebug
# APK 位置: app/build/outputs/apk/debug/app-debug.apk
```

---

## GitHub Actions 自动构建

### ⚙️ 配置 GitHub Secrets（首次使用必须）

在推送代码后，需要配置以下4个Secrets才能构建Release APK：

1. 进入 GitHub 仓库的 **Settings** 页面
2. 点击 **Secrets and variables** → **Actions**
3. 点击 **New repository secret** 添加以下密钥：

| Secret 名称 | 说明 | 获取方式 |
|------------|------|----------|
| `KEYSTORE_BASE64` | 签名密钥的Base64编码 | 运行 `./setup-github.sh` 后，查看 `keystore_base64.txt` 文件内容 |
| `KEYSTORE_PASSWORD` | Keystore 密码 | 默认：`YourKeystorePassword123` |
| `KEY_ALIAS` | Key 别名 | 默认：`design-assistant` |
| `KEY_PASSWORD` | Key 密码 | 默认：`YourKeyPassword456` |

#### 📝 详细配置步骤

如果您使用了自动化脚本 `setup-github.sh` 或 `setup-github.ps1`，脚本会在第7步提供完整的配置指南，包括：

1. **keystore_base64.txt** 文件的位置
2. 每个Secret的值
3. 如何在GitHub中添加Secret

#### 手动获取 KEYSTORE_BASE64

如果您没有运行自动化脚本，可以使用以下命令：

##### Windows (PowerShell)
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) | Out-File keystore_base64.txt
```

##### Mac/Linux
```bash
base64 -i release-keystore.jks > keystore_base64.txt
```

然后复制 `keystore_base64.txt` 的全部内容作为 `KEYSTORE_BASE64` 的值。

### 自动构建配置

本项目已配置 GitHub Actions，每次推送代码到 `main` 分支时会自动构建 Release APK。

### 获取自动构建的 APK

1. 进入 GitHub 仓库的 **Actions** 标签页
2. 选择最新的 **Build Release APK** 工作流
3. 在 **Artifacts** 部分下载 `app-release.apk`
4. APk 保留期为 90 天

### 手动触发构建

1. 进入 Actions 标签页
2. 选择 **Build Release APK**
3. 点击 **Run workflow** 按钮
4. 选择分支并点击 **Run workflow**

### 构建工作流说明

GitHub Actions 会执行以下操作：

1. **检出代码**：从仓库获取最新代码
2. **设置 JDK**：使用 JDK 17
3. **缓存 Gradle**：加速后续构建
4. **构建 Release APK**：执行 `assembleRelease`
5. **签名 APK**：使用配置的密钥签名
6. **上传 Artifact**：将APK作为构建产物上传

构建时间通常为 **5-10 分钟**，取决于网络速度和缓存命中率。

---

## 项目结构

```
app/src/main/
├── java/com/design/assistant/
│   ├── App.kt                          # 应用入口
│   ├── constants/
│   │   └── StandardConstants.kt        # 标准常量定义
│   ├── model/
│   │   ├── ProductType.kt              # 产品类型枚举
│   │   ├── DesignResult.kt             # 设计结果模型
│   │   ├── Gps028DesignParams.kt       # GPS028设计参数
│   │   ├── StdCompatibleTip.kt         # 多标准兼容建议
│   │   └── DynamicCrashItem.kt         # 碰撞测试项
│   ├── database/
│   │   ├── gps028/
│   │   │   ├── Gps028Database.kt
│   │   │   ├── entity/
│   │   │   └── dao/
│   │   └── travel/
│   │       └── childseat/
│   │           ├── eu/EceR129Database.kt
│   │           ├── us/Fmvss213Database.kt
│   │           ├── aus/AsNzs1754Database.kt
│   │           └── canada/Cmvss213Database.kt
│   ├── repository/
│   │   ├── gps028/
│   │   └── travel/
│   ├── viewmodel/
│   │   ├── ProductStandardSelectVM.kt
│   │   └── DesignGenerateVM.kt
│   └── ui/
│       ├── screens/
│       └── components/
├── res/                                # 资源文件
│   ├── drawable/
│   ├── layout/
│   ├── values/
│   └── mipmap/
└── AndroidManifest.xml                 # 应用清单文件
```

---

## 核心模块说明

### 1. 数据模型层 (model/)

定义所有业务实体和数据结构：

- `ProductType.kt`：产品类型枚举（出行类/家居类）
- `DesignResult.kt`：最终设计方案输出模型
- `Gps028DesignParams.kt`：GPS028设计参数（头枕/座宽/Envelop/侧防）
- `StdCompatibleTip.kt`：多标准兼容建议

### 2. 数据库层 (database/)

使用 Room 实现物理隔离的专属数据库：

- `gps028/`：GPS028专属库（儿童安全座椅设计参数）
- `travel/childseat/`：儿童安全座椅各标准库
  - `eu/`：ECE R129 数据库
  - `us/`：FMVSS 213 数据库
  - `aus/`：AS/NZS 1754 数据库
  - `canada/`：CMVSS 213 数据库

### 3. 仓库层 (repository/)

数据查询与整合，解耦 UI 和数据库：

- `gps028/`：GPS028数据仓库
- `travel/`：出行类产品仓库
- `home/`：家居类产品仓库
- `MultiStandardDesignRepository.kt`：全局多标准仓库

### 4. 视图模型层 (viewmodel/)

状态管理与业务逻辑：

- `ProductStandardSelectVM.kt`：产品/标准选择逻辑
- `DesignGenerateVM.kt`：设计方案生成逻辑

### 5. UI层 (ui/)

Jetpack Compose 界面实现：

- `screens/`：页面组件（选择页/结果页）
- `components/`：通用组件（卡片/标签/折叠面板）

---

## 数据库说明

### GPS028数据库

存储儿童安全座椅的专属设计参数：

| 字段 | 说明 | 示例值 |
|------|------|--------|
| dummyModel | 假人模型 | Q3, HIII-3YO |
| headrestHeight | 头枕高度范围 | 535-585mm |
| headrestBasePoint | 头枕高度基准点 | 坐骨结节（H点） |
| headrestTolerance | 头枕高度公差 | ±5mm |
| seatWidth | 有效座宽 | 350mm |
| seatWidthTotal | 总座宽（含侧防） | 420mm |
| envelopSize | ISOFIX Envelop等级 | ISOFIX Size Class B2 |
| envelopDetail | Envelop详细尺寸 | 纵向730mm(±10mm)，横向460mm(±5mm) |
| envelopStdClause | Envelop标准条款 | ECE R129 §5.3.2 |
| sideProtectionArea | 侧防面积 | ≥0.85m² |
| sideProtectionCover | 侧防覆盖区域 | T12胸部至P8头部 |
| sideProtectionStd | 侧防测试标准 | EN 14154-3:2022 |

### 数据库物理隔离

每个标准使用独立的数据库文件：

- `gps028_database.db` - GPS028设计参数
- `ece_r129_database.db` - 欧盟ECE R129
- `fmvss_213_database.db` - 美国FMVSS 213
- `as_nzs_1754_database.db` - 澳洲AS/NZS 1754
- `cmvss_213_database.db` - 加拿大CMVSS 213

---

## 使用指南

### 生成设计方案

1. **选择产品类型**
   - 展开对应产品卡片
   - 点击产品名称选中

2. **选择标准（支持多选）**
   - 在产品卡片内勾选需要的标准
   - 可使用"全选/取消全选"快捷操作

3. **输入儿童参数**
   - 输入儿童身高（1-150cm）
   - 输入儿童体重（1-50kg）

4. **生成设计方案**
   - 点击"生成设计方案"按钮
   - 等待系统计算完成

5. **查看结果**
   - 查看各标准的专业设计方案
   - 查看多标准兼容建议（如选择多个标准）

### 设计方案说明

每个设计方案包含以下层级：

#### 📦 产品标题
```
📦 儿童安全座椅设计方案（严格遵守ECE R129:2021 (欧盟i-Size)）
```

#### 【适用标准】（蓝色标签）
```
【适用标准】ECE R129:2021 (欧盟i-Size)
标准版本：2021版 | 实施要求：欧盟强制实施
🔍 核心要求：动态碰撞三向覆盖，侧防系统强制，ISOFIX接口兼容ISO 14530-3
```

#### 📊 基础适配数据
```
🔽 假人参数（ISO 13232-2:2021）
▫️ 假人模型：ECE R129 Q3假人
▫️ 百分位/年龄：50th百分位3-4岁儿童
▫️ 身高范围：87-105cm（用户输入90cm处于该范围中值，适配性最优）
▫️ 体重范围：13-18kg
▫️ 人体测量参数：坐高52cm，肩宽28cm，头围49cm
▫️ 安装方向：后向（ECE R129 §6.3（≤105cm儿童优先后向））
```

#### 📏 设计参数（GPS028-2023）
```
▫️ 头枕高度：535-585mm（基准点：坐骨结节（H点），公差：±5mm）
▫️ 座宽：有效座宽：350mm，总座宽（含侧防）：420mm
▫️ ISOFIX Envelop（盒子）尺寸：ISOFIX Size Class B2（ECE R129 §5.3.2）
▫️ Envelop详细尺寸：纵向长度730mm(±10mm)，横向宽度460mm(±5mm)，固定点间距300-350mm
▫️ 侧防面积要求：≥0.85m²（覆盖T12胸部至P8头部侧方区域）
▫️ 侧防测试标准：EN 14154-3:2022
```

#### ⚖️ 测试要求
```
▫️ 正面碰撞：碰撞速度50km/h(±1km/h)，碰撞台加速度15g(持续3ms)，HIC≤1000（ECE R129 §7.1.2）
▫️ 侧撞胸部压缩：侧撞速度60km/h(移动壁障)，胸部压缩量≤44mm，压缩速度≤2.5m/s（ECE R129 §7.1.3）
▫️ 安全带织带强度：纵向≥26.7kN，横向≥17.8kN（测试方法：ISO 6683:2017）
```

#### 🧪 标准测试项
```
动态碰撞：正碰
   测试设备：HYGE电动碰撞台（符合ISO 6487:2018）
   测试条件：假人后向，约束系统：ISOFIX+Top Tether，碰撞速度50km/h
   ✅ 合格判据：头部HIC≤1000，胸部加速度≤60g（3ms滑动平均）
```

---

## 多标准兼容建议

当选择多个标准时，系统会自动生成兼容设计建议：

```
📌 多标准兼容设计建议（ECE R129:2021 (欧盟i-Size) + GB 27887-2024 (中国儿童安全座椅新标)）

✅ 标准通用点（无需额外调整）
   ▫️ 动态碰撞正碰均要求HIC量化阈值，无模糊判据
   ▫️ 安全带织带均要求纵向/横向断裂强度，测试方法为ISO/SAE标准
   ▫️ 侧防系统均要求覆盖假人胸部至头部区域，含能量吸收装置

⚠️ 标准差异点（需重点关注）
   ▫️ GB 27887-2024要求≤105cm儿童强制后向（ECE仅优先）
   ▫️ GB侧撞胸部压缩量≤44mm（与ECE一致，但实施时间更晚，要求更严格）

💡 兼容设计建议（可直接采纳）
   ▫️ 按GB要求设计后向安装至105cm，头枕调节范围覆盖至585mm
   ▫️ 侧防系统按60km/h侧撞速度设计，同时满足ECE/GB
```

---

## 扩展开发

### 添加新产品

1. **更新 ProductType 枚举**
```kotlin
enum class ProductType(val typeName: String, val category: ProductCategory) {
    // 添加新产品
    NEW_PRODUCT("新产品名称", ProductCategory.CATEGORY)
}
```

2. **创建专属数据库**
```kotlin
@Database(entities = [NewProductEntity::class], version = 1)
abstract class NewProductDatabase : RoomDatabase() {
    abstract fun newProductDao(): NewProductDao
}
```

3. **创建 Repository**
```kotlin
class NewProductRepository(
    private val db: NewProductDatabase,
    private val gps028Repo: Gps028Repository
) {
    suspend fun generateProDesign(...) = DesignResult? { ... }
}
```

4. **更新全局仓库**
```kotlin
class MultiStandardDesignRepository(
    // 添加新产品仓库
    private val newProductRepo: NewProductRepository
) { ... }
```

### 添加新标准

1. **更新 StandardConstants**
```kotlin
object StandardConstants {
    const val NEW_STANDARD = "NEW_STANDARD"
    
    fun getStandardName(standardCode: String): String {
        when (standardCode) {
            NEW_STANDARD -> "新标准名称"
            // ...
        }
    }
    
    fun getStandardsByProduct(productType: ProductType): List<String> {
        when (productType) {
            ProductType.NEW_PRODUCT -> listOf(NEW_STANDARD)
            // ...
        }
    }
}
```

2. **创建标准专属数据库**
```kotlin
@Database(entities = [NewStandardEntity::class], version = 1)
abstract class NewStandardDatabase : RoomDatabase() { ... }
```

---

## 故障排查

### Gradle 同步失败

**问题**：Gradle 同步时下载依赖失败

**解决方案**：
```bash
# 清理 Gradle 缓存
./gradlew clean

# 重新同步
File -> Sync Project with Gradle Files
```

### 数据库初始化失败

**问题**：应用启动时数据库初始化失败

**解决方案**：
```kotlin
// 检查数据库版本号
@Database(entities = [...], version = 1)

// 开发阶段使用破坏性迁移
Room.databaseBuilder(...)
    .fallbackToDestructiveMigration()
    .build()
```

### APK 构建失败

**问题**：GitHub Actions 构建失败

**解决方案**：
1. 检查构建日志中的错误信息
2. 确保所有依赖版本兼容
3. 验证签名配置是否正确
4. 检查 `proguard-rules.pro` 文件

---

## 版本说明

### 当前版本：v1.0.0

**功能特性**：
- ✅ 四大品类完整支持
- ✅ 五大标准体系
- ✅ GPS028-2023数据库
- ✅ 专业级设计方案输出
- ✅ 多标准兼容建议
- ✅ GitHub Actions 自动构建

**技术栈**：
- Kotlin
- Jetpack Compose
- Room Database
- ViewModel & LiveData
- Coroutines

---

## 支持与反馈

如有问题或建议，请：

1. 提交 GitHub Issue
2. 发送邮件至：support@design-assistant.com
3. 加入开发者交流群：[群号]

---

## 许可证

本项目采用 MIT 许可证 - 详见 LICENSE 文件

---

## 致谢

- GPS028-2023 数据库提供方
- ECE R129 标准组织
- 中国汽车技术研究中心（GB 27887-2024）
- 美国国家公路交通安全管理局（FMVSS 213）
- 澳大利亚标准协会（AS/NZS 1754）
- 加拿大交通部（CMVSS 213）
