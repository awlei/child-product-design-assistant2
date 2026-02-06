# 项目文件清单

本文档列出了完整项目所需的所有文件，请确保以下文件都已创建。

---

## 必需文件清单

### 根目录文件

- [x] `README.md` - 项目说明文档
- [x] `DEPLOYMENT_GUIDE.md` - 部署指南
- [x] `build.gradle` - 项目级 Gradle 配置
- [x] `settings.gradle` - 项目设置
- [x] `gradle.properties` - Gradle 属性配置
- [x] `gradle/wrapper/gradle-wrapper.properties` - Gradle Wrapper 配置

### App 目录

#### 配置文件
- [x] `app/build.gradle` - 应用级 Gradle 配置
- [x] `app/proguard-rules.pro` - ProGuard 混淆规则（需创建）
- [x] `app/src/main/AndroidManifest.xml` - 应用清单

#### Kotlin 源代码

**应用入口**
- [x] `app/src/main/java/com/design/assistant/App.kt`

**常量层**
- [x] `app/src/main/java/com/design/assistant/constants/StandardConstants.kt`

**模型层 (model/)**
- [x] `app/src/main/java/com/design/assistant/model/ProductType.kt`
- [x] `app/src/main/java/com/design/assistant/model/DesignResult.kt`
- [x] `app/src/main/java/com/design/assistant/model/Gps028DesignParams.kt`
- [x] `app/src/main/java/com/design/assistant/model/StdCompatibleTip.kt`

**数据库层 (database/)**
- [x] `app/src/main/java/com/design/assistant/database/gps028/Gps028Database.kt`
- [x] `app/src/main/java/com/design/assistant/database/gps028/entity/Gps028DesignParamEntity.kt`
- [x] `app/src/main/java/com/design/assistant/database/gps028/dao/Gps028DesignDao.kt`

**ECE R129 数据库（示例）**
- [x] `app/src/main/java/com/design/assistant/database/travel/childseat/eu/EceR129Database.kt`
- [ ] `app/src/main/java/com/design/assistant/database/travel/childseat/eu/entity/EceR129DummyEntity.kt`（需创建）
- [ ] `app/src/main/java/com/design/assistant/database/travel/childseat/eu/entity/EceR129TestConfigEntity.kt`（需创建）
- [ ] `app/src/main/java/com/design/assistant/database/travel/childseat/eu/dao/EceR129DummyDao.kt`（需创建）
- [ ] `app/src/main/java/com/design/assistant/database/travel/childseat/eu/dao/EceR129TestConfigDao.kt`（需创建）

**其他标准数据库（结构类似 ECE R129）**
- [ ] `app/src/main/java/com/design/assistant/database/travel/childseat/us/`（FMVSS 213，需创建）
- [ ] `app/src/main/java/com/design/assistant/database/travel/childseat/aus/`（AS/NZS 1754，需创建）
- [ ] `app/src/main/java/com/design/assistant/database/travel/childseat/canada/`（CMVSS 213，需创建）

**仓库层 (repository/)**
- [ ] `app/src/main/java/com/design/assistant/repository/gps028/Gps028Repository.kt`（需创建）
- [ ] `app/src/main/java/com/design/assistant/repository/travel/ChildSeatRepository.kt`（需创建）
- [ ] `app/src/main/java/com/design/assistant/repository/MultiStandardDesignRepository.kt`（需创建）

**视图模型层 (viewmodel/)**
- [ ] `app/src/main/java/com/design/assistant/viewmodel/ProductStandardSelectVM.kt`（需创建）
- [ ] `app/src/main/java/com/design/assistant/viewmodel/DesignGenerateVM.kt`（需创建）

**UI层 (ui/)**
- [ ] `app/src/main/java/com/design/assistant/ui/screens/MainActivity.kt`（需创建）
- [ ] `app/src/main/java/com/design/assistant/ui/screens/StandardSelectScreen.kt`（需创建）
- [ ] `app/src/main/java/com/design/assistant/ui/screens/DesignResultScreen.kt`（需创建）
- [ ] `app/src/main/java/com/design/assistant/ui/components/ProDesignResultCard.kt`（需创建）
- [ ] `app/src/main/java/com/design/assistant/ui/components/StdCompatibleTipCard.kt`（需创建）
- [ ] `app/src/main/java/com/design/assistant/ui/components/ProductAccordion.kt`（需创建）

#### 资源文件 (res/)

**values/**
- [ ] `app/src/main/res/values/strings.xml`（需创建）
- [ ] `app/src/main/res/values/colors.xml`（需创建）
- [ ] `app/src/main/res/values/themes.xml`（需创建）

**mipmap/（应用图标）**
- [ ] `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`（需创建）
- [ ] `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`（需创建）
- [ ] `app/src/main/res/mipmap-hdpi/ic_launcher.png`（需创建）
- [ ] `app/src/main/res/mipmap-hdpi/ic_launcher_round.png`（需创建）
- [ ] `app/src/main/res/mipmap-mdpi/ic_launcher.png`（需创建）
- [ ] `app/src/main/res/mipmap-mdpi/ic_launcher_round.png`（需创建）
- [ ] `app/src/main/res/mipmap-xhdpi/ic_launcher.png`（需创建）
- [ ] `app/src/main/res/mipmap-xhdpi/ic_launcher_round.png`（需创建）
- [ ] `app/src/main/res/mipmap-xxhdpi/ic_launcher.png`（需创建）
- [ ] `app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png`（需创建）
- [ ] `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png`（需创建）
- [ ] `app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png`（需创建）

**drawable/（图标资源）**
- [ ] `app/src/main/res/drawable/ic_up.xml`（向上箭头，需创建）
- [ ] `app/src/main/res/drawable/ic_down.xml`（向下箭头，需创建）

### GitHub Actions

- [x] `.github/workflows/build-apk.yml` - 自动构建工作流

---

## 文件创建优先级

### 第一优先级（核心功能，必须先创建）

1. `App.kt` - 应用入口
2. `ProductType.kt` - 产品类型枚举
3. `StandardConstants.kt` - 标准常量
4. `DesignResult.kt` - 设计结果模型
5. `Gps028DesignParams.kt` - GPS028参数模型
6. `Gps028Database.kt` - GPS028数据库
7. `Gps028DesignParamEntity.kt` - GPS028实体
8. `Gps028DesignDao.kt` - GPS028 DAO

### 第二优先级（数据库与仓库）

9. ECE R129 数据库完整实现
10. 其他标准数据库（FMVSS/AS-NZS/CMVSS）
11. `Gps028Repository.kt` - GPS028仓库
12. `ChildSeatRepository.kt` - 儿童安全座椅仓库
13. `MultiStandardDesignRepository.kt` - 全局仓库

### 第三优先级（UI层）

14. `MainActivity.kt` - 主Activity
15. `ProductStandardSelectVM.kt` - 选择VM
16. `DesignGenerateVM.kt` - 生成VM
17. `StandardSelectScreen.kt` - 选择页
18. `DesignResultScreen.kt` - 结果页
19. UI组件（卡片、标签等）

### 第四优先级（资源与配置）

20. `strings.xml` - 字符串资源
21. `colors.xml` - 颜色资源
22. `themes.xml` - 主题配置
23. 应用图标
24. ProGuard 配置

---

## 快速创建模板

### 数据库实体模板

```kotlin
package com.design.assistant.database.xxx.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "xxx_table")
data class XxxEntity(
    @PrimaryKey val id: String,
    val field1: String,
    val field2: Int
)
```

### DAO 模板

```kotlin
package com.design.assistant.database.xxx.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface XxxDao {
    @Query("SELECT * FROM xxx_table WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): XxxEntity?

    @Insert
    suspend fun insert(item: XxxEntity)
}
```

### Repository 模板

```kotlin
package com.design.assistant.repository.xxx

import com.design.assistant.database.xxx.XxxDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class XxxRepository(private val db: XxxDatabase) {
    private val dao = db.xxxDao()

    suspend fun getById(id: String) = withContext(Dispatchers.IO) {
        dao.getById(id)
    }
}
```

---

## 创建后的验证步骤

### 1. 目录结构检查

```bash
# 查看目录树
tree app/src/main/java/com/design/assistant/
```

### 2. 编译检查

```bash
# 在项目根目录执行
./gradlew build --dry-run
```

### 3. 语法检查

```bash
# 检查 Kotlin 代码
./gradlew compileDebugKotlin
```

### 4. 数据库验证

确保所有数据库都满足：
- [ ] 继承 `RoomDatabase`
- [ ] 有 `@Database` 注解
- [ ] 定义了 `DATABASE_NAME`
- [ ] 所有实体都注册
- [ ] 抽象方法返回正确的 DAO

---

## 文件命名规范

- Kotlin 文件：`PascalCase.kt`
- 资源文件：`snake_case.xml`
- 布局文件：`activity_*.xml`、`fragment_*.xml`
- 常量类：以 `Constants` 结尾
- 实体类：以 `Entity` 结尾
- DAO 接口：以 `Dao` 结尾
- Repository 类：以 `Repository` 结尾
- ViewModel 类：以 `VM` 或 `ViewModel` 结尾

---

## 版本控制建议

### 提交策略

```bash
# 按功能模块提交
git add app/src/main/java/com/design/assistant/model/
git commit -m "feat: 添加数据模型层"

git add app/src/main/java/com/design/assistant/database/
git commit -m "feat: 添加数据库层"

git add app/src/main/java/com/design/assistant/repository/
git commit -m "feat: 添加仓库层"
```

### 分支策略

- `main` - 稳定版本
- `develop` - 开发版本
- `feature/xxx` - 功能分支
- `bugfix/xxx` - 修复分支

---

**清单状态**: 已创建 21/51 文件（41%）
**下一步**: 按优先级继续创建剩余文件
