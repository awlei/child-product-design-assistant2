# 儿童产品设计助手 - 联网搜索功能设计文档

**设计日期**: 2025-01-22
**功能模块**: 品牌设计参数联网搜索
**技术方案**: Retrofit + DuckDuckGo API / Wikipedia API

---

## 一、功能概述

### 1.1 功能目标

为专业儿童产品设计工程师提供联网搜索功能，参考各大品牌的同类产品设计参数，包括：

- **儿童安全座椅**: Britax, Cybex, Graco, Maxi-Cosi, Chicco 等
- **婴儿推车**: Bugaboo, Stokke, Silver Cross, UPPAbaby, Joie 等
- **儿童高脚椅**: Stokke, Cybex, Chicco, Hauck, Safety 1st 等
- **儿童床**: IKEA, Stokke, BabyBjörn, Silver Cross, Hauck 等

### 1.2 核心功能

1. **按产品类型搜索**: 根据当前选择的产品类型搜索相关品牌产品
2. **品牌筛选**: 支持按品牌筛选搜索结果
3. **参数对比**: 对比不同品牌产品的设计参数
4. **参数提取**: 从搜索结果中提取关键设计参数
5. **收藏功能**: 收藏有价值的设计参考

---

## 二、技术方案

### 2.1 技术栈

- **网络请求**: Retrofit 2.9.0 + OkHttp 4.10.0
- **JSON 解析**: Gson 2.10.1
- **协程**: Kotlin Coroutines + Flow
- **UI 状态**: Jetpack Compose + ViewModel

### 2.2 数据源方案

由于 Google Custom Search API 和 Bing Search API 需要 API Key 和付费配额，我们采用以下免费数据源：

#### 方案 1: DuckDuckGo Instant Answer API（推荐）

**优点**:
- ✅ 免费，无需 API Key
- ✅ 无需认证
- ✅ 返回即时答案
- ✅ 支持多种查询方式

**限制**:
- ⚠️ 搜索结果有限
- ⚠️ 不支持深度搜索

**适用场景**: 快速获取品牌官网产品信息

#### 方案 2: Wikipedia API

**优点**:
- ✅ 免费，无需 API Key
- ✅ 内容权威
- ✅ 支持多语言
- ✅ 结构化数据

**限制**:
- ⚠️ 主要用于百科信息，不适合商业产品搜索
- ⚠️ 数据更新较慢

**适用场景**: 获取标准、法规的详细解释

#### 方案 3: 产品数据库 API（未来扩展）

**可考虑的 API**:
- Amazon Product Advertising API（需要注册）
- eBay Finding API（需要注册）
- Google Shopping API（需要注册）

---

## 三、数据模型设计

### 3.1 搜索结果模型

```kotlin
/**
 * 品牌产品搜索结果
 */
data class BrandProductSearchResult(
    val brand: String,              // 品牌名称
    val productName: String,        // 产品名称
    val productType: ProductType,   // 产品类型
    val modelNumber: String?,       // 型号
    val designParams: DesignParams?, // 设计参数
    val imageUrl: String?,          // 产品图片 URL
    val productUrl: String?,        // 产品详情页 URL
    val description: String?,       // 产品描述
    val specs: List<ProductSpec>?,  // 详细规格
    val price: String?,             // 价格
    val rating: Float?,             // 评分
    val source: SearchResultSource, // 数据来源
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 设计参数（从搜索结果提取）
 */
data class DesignParams(
    val height: String?,           // 高度
    val width: String?,            // 宽度
    val depth: String?,            // 深度
    val weight: String?,           // 重量
    val ageRange: String?,         // 适用年龄
    val weightCapacity: String?,   // 承重
    val headrestAdjustment: String?, // 头枕调节
    val seatWidth: String?,        // 座宽
    val envelopeSize: String?,     // Envelope 尺寸
    val certification: String?,    // 认证标准
    val additionalInfo: Map<String, String>? // 其他信息
)

/**
 * 产品规格
 */
data class ProductSpec(
    val name: String,              // 规格名称
    val value: String,             // 规格值
    val unit: String?              // 单位
)

/**
 * 数据来源
 */
enum class SearchResultSource {
    DUCK_DUCK_GO,                   // DuckDuckGo
    WIKIPEDIA,                      // Wikipedia
    AMAZON,                         // Amazon（未来）
    EBAY,                           // eBay（未来）
    GOOGLE_SHOPPING,                // Google Shopping（未来）
    MANUAL_INPUT                    // 手动输入
}
```

### 3.2 搜索请求模型

```kotlin
/**
 * 搜索请求参数
 */
data class SearchRequest(
    val productType: ProductType,    // 产品类型
    val brand: String?,             // 品牌（可选）
    val query: String?,             // 自定义查询词（可选）
    val minHeight: Int?,            // 最小身高过滤
    val maxHeight: Int?,            // 最大身高过滤
    val minWeight: Double?,         // 最小体重过滤
    val maxWeight: Double?,         // 最大体重过滤
    val standard: String?           // 标准过滤（如 ECE R129）
)
```

---

## 四、API 接口设计

### 4.1 DuckDuckGo API

```kotlin
/**
 * DuckDuckGo Instant Answer API 接口
 */
interface DuckDuckGoApiService {
    /**
     * 搜索即时答案
     * @param q 查询词
     * @return 搜索结果
     */
    @GET("https://api.duckduckgo.com/")
    suspend fun searchInstantAnswer(
        @Query("q") q: String,
        @Query("format") format: String = "json",
        @Query("no_html") noHtml: Int = 1,
        @Query("skip_disambig") skipDisambig: Int = 1
    ): DuckDuckGoResponse
}

/**
 * DuckDuckGo 搜索响应
 */
data class DuckDuckGoResponse(
    @SerializedName("AbstractText") val abstractText: String?,
    @SerializedName("AbstractSource") val abstractSource: String?,
    @SerializedName("AbstractURL") val abstractUrl: String?,
    @SerializedName("Image") val image: String?,
    @SerializedName("Heading") val heading: String?,
    @SerializedName("Answer") val answer: String?,
    @SerializedName("AnswerType") val answerType: String?,
    @SerializedName("Definition") val definition: String?,
    @SerializedName("DefinitionSource") val definitionSource: String?,
    @SerializedName("DefinitionURL") val definitionUrl: String?,
    @SerializedName("RelatedTopics") val relatedTopics: List<RelatedTopic>?
)

/**
 * 相关主题
 */
data class RelatedTopic(
    @SerializedName("FirstURL") val firstUrl: String?,
    @SerializedName("Text") val text: String?,
    @SerializedName("Icon") val icon: Icon?
)

/**
 * 图标
 */
data class Icon(
    @SerializedName("URL") val url: String?
)
```

### 4.2 Wikipedia API

```kotlin
/**
 * Wikipedia API 接口
 */
interface WikipediaApiService {
    /**
     * 搜索 Wikipedia
     * @param query 查询词
     * @param limit 结果数量
     * @return 搜索结果
     */
    @GET("https://en.wikipedia.org/w/api.php")
    suspend fun searchWikipedia(
        @Query("action") action: String = "query",
        @Query("list") list: String = "search",
        @Query("srsearch") srsearch: String,
        @Query("srlimit") srlimit: Int = 10,
        @Query("format") format: String = "json",
        @Query("utf8") utf8: Int = 1
    ): WikipediaSearchResponse

    /**
     * 获取 Wikipedia 页面详情
     * @param pageId 页面 ID
     * @return 页面详情
     */
    @GET("https://en.wikipedia.org/w/api.php")
    suspend fun getPageDetails(
        @Query("action") action: String = "query",
        @Query("prop") prop: String = "extracts|pageimages",
        @Query("pageids") pageids: String,
        @Query("exintro") exintro: Int = 1,
        @Query("explaintext") explaintext: Int = 1,
        @Query("piprop") piprop: String = "original",
        @Query("format") format: String = "json",
        @Query("utf8") utf8: Int = 1
    ): WikipediaPageResponse
}

/**
 * Wikipedia 搜索响应
 */
data class WikipediaSearchResponse(
    @SerializedName("query") val query: WikipediaQuery
)

data class WikipediaQuery(
    @SerializedName("searchinfo") val searchInfo: WikipediaSearchInfo?,
    @SerializedName("search") val search: List<WikipediaSearchResult>?
)

data class WikipediaSearchInfo(
    @SerializedName("totalhits") val totalHits: Int?
)

data class WikipediaSearchResult(
    @SerializedName("pageid") val pageId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("snippet") val snippet: String?,
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("wordcount") val wordCount: Int?
)

/**
 * Wikipedia 页面响应
 */
data class WikipediaPageResponse(
    @SerializedName("query") val query: WikipediaPageQuery
)

data class WikipediaPageQuery(
    @SerializedName("pages") val pages: Map<String, WikipediaPage>
)

data class WikipediaPage(
    @SerializedName("pageid") val pageId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("extract") val extract: String?,
    @SerializedName("thumbnail") val thumbnail: WikipediaThumbnail?
)

data class WikipediaThumbnail(
    @SerializedName("source") val source: String?,
    @SerializedName("width") val width: Int?,
    @SerializedName("height") val height: Int?
)
```

---

## 五、搜索策略设计

### 5.1 搜索查询构建

根据产品类型构建智能查询词：

```kotlin
/**
 * 构建搜索查询词
 */
fun buildSearchQuery(
    productType: ProductType,
    brand: String? = null,
    customQuery: String? = null
): List<String> {
    val baseQuery = if (customQuery != null) {
        customQuery
    } else {
        when (productType) {
            ProductType.CHILD_SEAT -> "child safety seat car seat"
            ProductType.BABY_STROLLER -> "baby stroller pram pushchair"
            ProductType.HIGH_CHAIR -> "baby high chair feeding chair"
            ProductType.CHILD_BED -> "crib toddler bed baby bed"
        }
    }

    val queries = mutableListOf<String>()

    // 基础查询
    queries.add(baseQuery)

    // 如果指定了品牌，添加品牌查询
    brand?.let {
        queries.add("$brand $baseQuery")
        queries.add("$brand ${baseQuery.replace("baby ", "")}")
    }

    // 添加规格相关查询
    queries.add("$baseQuery specifications dimensions")
    queries.add("$baseQuery manual pdf")

    return queries.distinct()
}
```

### 5.2 参数提取策略

从搜索结果中提取设计参数：

```kotlin
/**
 * 从文本中提取设计参数
 */
fun extractDesignParamsFromText(
    text: String,
    productType: ProductType
): DesignParams? {
    val params = mutableMapOf<String, String>()

    // 使用正则表达式提取数值
    val heightPattern = Regex("""(?:height|高度|H)[:\s]*(\d+(?:\.\d+)?)\s*(cm|mm|inch)""")
    val weightPattern = Regex("""(?:weight|weight capacity|承重|重量|W)[:\s]*(\d+(?:\.\d+)?)\s*(kg|lb)""")
    val widthPattern = Regex("""(?:width|width|宽度|W)[:\s]*(\d+(?:\.\d+)?)\s*(cm|mm|inch)""")
    val depthPattern = Regex("""(?:depth|length|length|深度|长度|D|L)[:\s]*(\d+(?:\.\d+)?)\s*(cm|mm|inch)""")
    val agePattern = Regex("""(?:age|age range|适用年龄)[:\s]*(\d+(?:-\d+)?)\s*(?:months?|years?|months?|years?)""")

    // 提取高度
    heightPattern.find(text)?.let { match ->
        params["height"] = "${match.groupValues[1]}${match.groupValues[2]}"
    }

    // 提取重量
    weightPattern.find(text)?.let { match ->
        params["weightCapacity"] = "${match.groupValues[1]}${match.groupValues[2]}"
    }

    // 提取宽度
    widthPattern.find(text)?.let { match ->
        params["width"] = "${match.groupValues[1]}${match.groupValues[2]}"
    }

    // 提取深度/长度
    depthPattern.find(text)?.let { match ->
        params["depth"] = "${match.groupValues[1]}${match.groupValues[2]}"
    }

    // 提取年龄范围
    agePattern.find(text)?.let { match ->
        params["ageRange"] = match.value
    }

    return if (params.isNotEmpty()) {
        DesignParams(
            height = params["height"],
            width = params["width"],
            depth = params["depth"],
            weight = params["weightCapacity"],
            ageRange = params["ageRange"],
            additionalInfo = params
        )
    } else {
        null
    }
}
```

---

## 六、UI 设计

### 6.1 搜索页面结构

```
┌─────────────────────────────────────────┐
│ ← 品牌参考                [筛选] [收藏]  │
├─────────────────────────────────────────┤
│ [搜索框]                                │
│ "儿童安全座椅"                         │
├─────────────────────────────────────────┤
│ 品牌筛选                                │
│ [✓] Britax [ ] Cybex [ ] Graco         │
│ [ ] Maxi-Cosi [ ] Chicco [ ] 更多 >    │
├─────────────────────────────────────────┤
│ 搜索结果 (5)                            │
│ ┌─────────────────────────────────────┐ │
│ │ [图片] Britax Römer Dualfix i-Size  │ │
│ │ 品牌标识                              │ │
│ │ • 型号：Dualfix i-Size               │ │
│ │ • 身高：40-105 cm                   │ │
│ │ • 体重：0-18 kg                     │ │
│ │ • 标准：ECE R129                    │ │
│ │ [查看详情] [收藏] [对比]             │ │
│ └─────────────────────────────────────┘ │
│ ┌─────────────────────────────────────┐ │
│ │ [图片] Cybex Solution S-Fix        │ │
│ │ 品牌标识                              │ │
│ │ • 型号：Solution S-Fix              │ │
│ │ • 身高：61-105 cm                   │ │
│ │ • 体重：9-18 kg                     │ │
│ │ • 标准：ECE R129                    │ │
│ │ [查看详情] [收藏] [对比]             │ │
│ └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

### 6.2 搜索结果卡片

```kotlin
@Composable
fun BrandProductSearchResultCard(
    result: BrandProductSearchResult,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    onCompare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 产品图片
            result.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = result.productName,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            // 产品信息
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 品牌和产品名称
                Text(
                    text = "${result.brand} - ${result.productName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                result.modelNumber?.let {
                    Text(
                        text = "型号：$it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 设计参数
                result.designParams?.let { params ->
                    ParamRow("身高", params.height)
                    ParamRow("体重", params.weightCapacity)
                    ParamRow("标准", params.certification)
                }

                // 评分
                result.rating?.let { rating ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = " $rating",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // 操作按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("查看详情")
            }
            OutlinedButton(
                onClick = onFavorite,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("收藏")
            }
            OutlinedButton(
                onClick = onCompare,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Compare,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("对比")
            }
        }
    }
}

@Composable
fun ParamRow(label: String, value: String?) {
    if (value != null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$label：",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
```

---

## 七、品牌数据库

### 7.1 品牌信息

```kotlin
/**
 * 品牌信息数据库
 */
object BrandDatabase {
    /**
     * 儿童安全座椅品牌
     */
    val childSeatBrands = listOf(
        BrandInfo(
            name = "Britax Römer",
            country = "德国",
            website = "https://www.britax-roemer.com",
            logoUrl = "https://www.britax-roemer.com/favicon.ico",
            knownFor = "高品质安全性",
            popularProducts = listOf("Dualfix i-Size", "Kidfix i-Size", "Römer Advansafix")
        ),
        BrandInfo(
            name = "Cybex",
            country = "德国",
            website = "https://www.cybex-online.com",
            logoUrl = "https://www.cybex-online.com/favicon.ico",
            knownFor = "创新设计+安全",
            popularProducts = listOf("Solution S-Fix", "Aton B", "Cloud T")
        ),
        BrandInfo(
            name = "Graco",
            country = "美国",
            website = "https://www.gracobaby.com",
            logoUrl = "https://www.gracobaby.com/favicon.ico",
            knownFor = "性价比高",
            popularProducts = listOf("4Ever DLX", "SnugRide 35", "TurboBooster")
        ),
        BrandInfo(
            name = "Maxi-Cosi",
            country = "荷兰",
            website = "https://www.maxi-cosi.com",
            logoUrl = "https://www.maxi-cosi.com/favicon.ico",
            knownFor = "欧洲领先品牌",
            popularProducts = listOf("Pearl 360", "Pebble 360", "Tobi")
        ),
        BrandInfo(
            name = "Chicco",
            country = "意大利",
            website = "https://www.chicco.com",
            logoUrl = "https://www.chicco.com/favicon.ico",
            knownFor = "意大利设计",
            popularProducts = listOf("NextFit", "KeyFit 30", "Cortina")
        ),
        BrandInfo(
            name = "Recaro",
            country = "德国",
            website = "https://www.recaro-cs.com",
            logoUrl = "https://www.recaro-cs.com/favicon.ico",
            knownFor = "汽车座椅专家",
            popularProducts = listOf "Monza Nova", "Privia", "Young Sport Hero"
        ),
        BrandInfo(
            name = "Nuna",
            country = "荷兰",
            website = "https://www.nuna.eu",
            logoUrl = "https://www.nuna.eu/favicon.ico",
            knownFor = "现代简约设计",
            popularProducts = listOf("REBL Plus", "PIVOT X", "Rava")
        ),
        BrandInfo(
            name = "Clek",
            country = "加拿大",
            website = "https://www.clek.com",
            logoUrl = "https://www.clek.com/favicon.ico",
            knownFor = "北美安全标准",
            popularProducts = listOf("Foonf", "Fllo", "Oobr")
        )
    )

    /**
     * 婴儿推车品牌
     */
    val strollerBrands = listOf(
        BrandInfo(
            name = "Bugaboo",
            country = "荷兰",
            website = "https://www.bugaboo.com",
            logoUrl = "https://www.bugaboo.com/favicon.ico",
            knownFor = "高端设计",
            popularProducts = listOf("Bee 6", "Fox 5", "Donkey 5")
        ),
        BrandInfo(
            name = "Stokke",
            country = "挪威",
            website = "https://www.stokke.com",
            logoUrl = "https://www.stokke.com/favicon.ico",
            knownFor = "北欧设计",
            popularProducts = listOf("Xplory", "Trailz", "Crusi")
        ),
        BrandInfo(
            name = "Silver Cross",
            country = "英国",
            website = "https://www.silvercrossbaby.com",
            logoUrl = "https://www.silvercrossbaby.com/favicon.ico",
            knownFor = "英式皇家品质",
            popularProducts = listOf("Wave", "Reflex", "Jet")
        ),
        BrandInfo(
            name = "UPPAbaby",
            country = "美国",
            website = "https://www.uppababy.com",
            logoUrl = "https://www.uppababy.com/favicon.ico",
            knownFor = "美式实用",
            popularProducts = listOf("Vista V2", "Cruz V2", "Minu V2")
        ),
        BrandInfo(
            name = "Joie",
            country = "英国",
            website = "https://www.joiebaby.com",
            logoUrl = "https://www.joiebaby.com/favicon.ico",
            knownFor = "性价比高",
            popularProducts = listOf("Versatrax", "Litetrax 4", "Sakura")
        )
    )

    /**
     * 儿童高脚椅品牌
     */
    val highChairBrands = listOf(
        BrandInfo(
            name = "Stokke",
            country = "挪威",
            website = "https://www.stokke.com",
            logoUrl = "https://www.stokke.com/favicon.ico",
            knownFor = "Tripp Trapp 系列",
            popularProducts = listOf("Tripp Trapp", "Clikk", "Steps")
        ),
        BrandInfo(
            name = "Cybex",
            country = "德国",
            website = "https://www.cybex-online.com",
            logoUrl = "https://www.cybex-online.com/favicon.ico",
            knownFor = "Lemo 系列",
            popularProducts = listOf("Lemo", "Balios S", "Silver")
        ),
        BrandInfo(
            name = "Chicco",
            country = "意大利",
            website = "https://www.chicco.com",
            logoUrl = "https://www.chicco.com/favicon.ico",
            knownFor = "Polly 系列",
            popularProducts = listOf("Polly 2Start", "Pocket Snack", "Hook On")
        ),
        BrandInfo(
            name = "Hauck",
            country = "德国",
            website = "https://www.hauck.de",
            logoUrl = "https://www.hauck.de/favicon.ico",
            knownFor = "德国品质",
            popularProducts = listOf("Alpha+", "Beta+", "Nacelle")
        ),
        BrandInfo(
            name = "Safety 1st",
            country = "美国",
            website = "https://www.safety1st.com",
            logoUrl = "https://www.safety1st.com/favicon.ico",
            knownFor = "安全第一",
            popularProducts = listOf("Timba", "Eat", "Grow")
        )
    )

    /**
     * 儿童床品牌
     */
    val childBedBrands = listOf(
        BrandInfo(
            name = "IKEA",
            country = "瑞典",
            website = "https://www.ikea.com",
            logoUrl = "https://www.ikea.com/favicon.ico",
            knownFor = "性价比高",
            popularProducts = listOf("Gulliver", "Sniglar", "Sundvik")
        ),
        BrandInfo(
            name = "Stokke",
            country = "挪威",
            website = "https://www.stokke.com",
            logoUrl = "https://www.stokke.com/favicon.ico",
            knownFor = "Sleepi 系列",
            popularProducts = listOf("Sleepi", "Home", "Crib")
        ),
        BrandInfo(
            name = "BabyBjörn",
            country = "瑞典",
            website = "https://www.babybjorn.com",
            logoUrl = "https://www.babybjorn.com/favicon.ico",
            knownFor = "瑞典设计",
            popularProducts = listOf("Travel Crib Light", "Crib", "Bassinet")
        ),
        BrandInfo(
            name = "Silver Cross",
            country = "英国",
            website = "https://www.silvercrossbaby.com",
            logoUrl = "https://www.silvercrossbaby.com/favicon.ico",
            knownFor = "英式皇家",
            popularProducts = listOf("Cot Bed", "Harmony", "Nostalgia")
        ),
        BrandInfo(
            name = "Hauck",
            country = "德国",
            website = "https://www.hauck.de",
            logoUrl = "https://www.hauck.de/favicon.ico",
            knownFor = "德国品质",
            popularProducts = listOf("Beta+", "Dream N Play", "Affinity")
        )
    )

    /**
     * 根据产品类型获取品牌列表
     */
    fun getBrandsByProductType(productType: ProductType): List<BrandInfo> {
        return when (productType) {
            ProductType.CHILD_SEAT -> childSeatBrands
            ProductType.BABY_STROLLER -> strollerBrands
            ProductType.HIGH_CHAIR -> highChairBrands
            ProductType.CHILD_BED -> childBedBrands
        }
    }
}

/**
 * 品牌信息
 */
data class BrandInfo(
    val name: String,
    val country: String,
    val website: String,
    val logoUrl: String?,
    val knownFor: String,
    val popularProducts: List<String>
)
```

---

## 八、实现步骤

### Phase 1: 基础架构（第 1-2 天）

1. ✅ 创建数据模型
   - `BrandProductSearchResult`
   - `DesignParams`
   - `ProductSpec`
   - `BrandInfo`

2. ✅ 添加依赖
   - Retrofit 2.9.0
   - OkHttp 4.10.0
   - Gson 2.10.1
   - Coil (图片加载)

3. ✅ 创建 API 服务
   - `DuckDuckGoApiService`
   - `WikipediaApiService`
   - `RetrofitClient`

### Phase 2: 搜索功能（第 3-4 天）

1. ✅ 创建 Repository
   - `WebSearchRepository`
   - 实现搜索逻辑
   - 实现参数提取

2. ✅ 创建 ViewModel
   - `BrandSearchViewModel`
   - 管理搜索状态
   - 处理收藏功能

3. ✅ 创建 UI
   - `BrandSearchScreen`
   - `BrandProductSearchResultCard`
   - `BrandFilterDialog`

### Phase 3: 对比功能（第 5-6 天）

1. ✅ 创建对比页面
   - `ProductComparisonScreen`
   - 对比表格
   - 参数高亮显示

2. ✅ 优化用户体验
   - 加载状态
   - 错误处理
   - 空状态提示

### Phase 4: 测试与优化（第 7 天）

1. ✅ 单元测试
2. ✅ 集成测试
3. ✅ UI 测试
4. ✅ 性能优化

---

## 九、后续扩展

### 9.1 高级功能

1. **AI 智能分析**: 使用 AI 分析搜索结果，自动推荐最佳设计参数
2. **图片识别**: 上传产品图片，自动识别品牌和型号
3. **价格追踪**: 追踪产品价格变化
4. **用户社区**: 用户分享产品评测和参数

### 9.2 数据源扩展

1. **Amazon API**: 集成亚马逊产品数据库
2. **eBay API**: 集成 eBay 产品数据库
3. **Google Shopping API**: 集成 Google Shopping
4. **品牌官方 API**: 直接对接品牌官方数据

---

## 十、注意事项

### 10.1 法律合规

- ⚠️ 搜索结果仅用于参考，不得用于商业用途
- ⚠️ 尊重品牌知识产权
- ⚠️ 标注数据来源

### 10.2 数据准确性

- ⚠️ 搜索结果可能不准确，需人工验证
- ⚠️ 提供手动修正功能
- ⚠️ 建立用户反馈机制

### 10.3 隐私保护

- ⚠️ 不收集用户搜索历史
- ⚠� 不存储用户数据
- ⚠️ 使用匿名搜索

---

## 十一、总结

本设计文档提供了完整的联网搜索功能设计方案，包括：

- ✅ 技术方案（Retrofit + DuckDuckGo/Wikipedia API）
- ✅ 数据模型（搜索结果、设计参数、品牌信息）
- ✅ API 接口（DuckDuckGo、Wikipedia）
- ✅ 搜索策略（查询构建、参数提取）
- ✅ UI 设计（搜索页面、结果卡片、对比功能）
- ✅ 品牌数据库（四大品类，20+ 品牌）
- ✅ 实现步骤（4 个 Phase，7 天）

**预期效果**: 为专业儿童产品设计工程师提供便捷的联网搜索功能，参考各大品牌的设计参数，提升设计效率和质量。

---

**文档版本**: v1.0
**设计日期**: 2025-01-22
**状态**: ✅ 设计完成，待实现
