package com.design.assistant.data.model

import com.google.gson.annotations.SerializedName

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

// ==================== DuckDuckGo API Models ====================

/**
 * DuckDuckGo Instant Answer API 接口
 */
interface DuckDuckGoApiService {
    /**
     * 搜索即时答案
     * @param q 查询词
     * @return 搜索结果
     */
    @retrofit2.http.GET("/")
    suspend fun searchInstantAnswer(
        @retrofit2.http.Query("q") q: String,
        @retrofit2.http.Query("format") format: String = "json",
        @retrofit2.http.Query("no_html") noHtml: Int = 1,
        @retrofit2.http.Query("skip_disambig") skipDisambig: Int = 1
    ): DuckDuckGoResponse
}

/**
 * DuckDuckGo 搜索响应
 */
data class DuckDuckGoResponse(
    @SerializedName("AbstractText")
    val abstractText: String?,

    @SerializedName("AbstractSource")
    val abstractSource: String?,

    @SerializedName("AbstractURL")
    val abstractUrl: String?,

    @SerializedName("Image")
    val image: String?,

    @SerializedName("Heading")
    val heading: String?,

    @SerializedName("Answer")
    val answer: String?,

    @SerializedName("AnswerType")
    val answerType: String?,

    @SerializedName("Definition")
    val definition: String?,

    @SerializedName("DefinitionSource")
    val definitionSource: String?,

    @SerializedName("DefinitionURL")
    val definitionUrl: String?,

    @SerializedName("RelatedTopics")
    val relatedTopics: List<RelatedTopic>?
)

/**
 * 相关主题
 */
data class RelatedTopic(
    @SerializedName("FirstURL")
    val firstUrl: String?,

    @SerializedName("Text")
    val text: String?,

    @SerializedName("Icon")
    val icon: Icon?
)

/**
 * 图标
 */
data class Icon(
    @SerializedName("URL")
    val url: String?
)

// ==================== Wikipedia API Models ====================

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
    @retrofit2.http.GET("w/api.php")
    suspend fun searchWikipedia(
        @retrofit2.http.Query("action") action: String = "query",
        @retrofit2.http.Query("list") list: String = "search",
        @retrofit2.http.Query("srsearch") srsearch: String,
        @retrofit2.http.Query("srlimit") srlimit: Int = 10,
        @retrofit2.http.Query("format") format: String = "json",
        @retrofit2.http.Query("utf8") utf8: Int = 1
    ): WikipediaSearchResponse

    /**
     * 获取 Wikipedia 页面详情
     * @param pageId 页面 ID
     * @return 页面详情
     */
    @retrofit2.http.GET("w/api.php")
    suspend fun getPageDetails(
        @retrofit2.http.Query("action") action: String = "query",
        @retrofit2.http.Query("prop") prop: String = "extracts|pageimages",
        @retrofit2.http.Query("pageids") pageids: String,
        @retrofit2.http.Query("exintro") exintro: Int = 1,
        @retrofit2.http.Query("explaintext") explaintext: Int = 1,
        @retrofit2.http.Query("piprop") piprop: String = "original",
        @retrofit2.http.Query("format") format: String = "json",
        @retrofit2.http.Query("utf8") utf8: Int = 1
    ): WikipediaPageResponse
}

/**
 * Wikipedia 搜索响应
 */
data class WikipediaSearchResponse(
    @SerializedName("query")
    val query: WikipediaQuery
)

data class WikipediaQuery(
    @SerializedName("searchinfo")
    val searchInfo: WikipediaSearchInfo?,

    @SerializedName("search")
    val search: List<WikipediaSearchResult>?
)

data class WikipediaSearchInfo(
    @SerializedName("totalhits")
    val totalHits: Int?
)

data class WikipediaSearchResult(
    @SerializedName("pageid")
    val pageId: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("snippet")
    val snippet: String?,

    @SerializedName("timestamp")
    val timestamp: String?,

    @SerializedName("wordcount")
    val wordCount: Int?
)

/**
 * Wikipedia 页面响应
 */
data class WikipediaPageResponse(
    @SerializedName("query")
    val query: WikipediaPageQuery
)

data class WikipediaPageQuery(
    @SerializedName("pages")
    val pages: Map<String, WikipediaPage>
)

data class WikipediaPage(
    @SerializedName("pageid")
    val pageId: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("extract")
    val extract: String?,

    @SerializedName("thumbnail")
    val thumbnail: WikipediaThumbnail?
)

data class WikipediaThumbnail(
    @SerializedName("source")
    val source: String?,

    @SerializedName("width")
    val width: Int?,

    @SerializedName("height")
    val height: Int?
)

// ==================== Brand Information ====================

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
            logoUrl = null,
            knownFor = "高品质安全性",
            popularProducts = listOf("Dualfix i-Size", "Kidfix i-Size", "Römer Advansafix")
        ),
        BrandInfo(
            name = "Cybex",
            country = "德国",
            website = "https://www.cybex-online.com",
            logoUrl = null,
            knownFor = "创新设计+安全",
            popularProducts = listOf("Solution S-Fix", "Aton B", "Cloud T")
        ),
        BrandInfo(
            name = "Graco",
            country = "美国",
            website = "https://www.gracobaby.com",
            logoUrl = null,
            knownFor = "性价比高",
            popularProducts = listOf("4Ever DLX", "SnugRide 35", "TurboBooster")
        ),
        BrandInfo(
            name = "Maxi-Cosi",
            country = "荷兰",
            website = "https://www.maxi-cosi.com",
            logoUrl = null,
            knownFor = "欧洲领先品牌",
            popularProducts = listOf("Pearl 360", "Pebble 360", "Tobi")
        ),
        BrandInfo(
            name = "Chicco",
            country = "意大利",
            website = "https://www.chicco.com",
            logoUrl = null,
            knownFor = "意大利设计",
            popularProducts = listOf("NextFit", "KeyFit 30", "Cortina")
        ),
        BrandInfo(
            name = "Recaro",
            country = "德国",
            website = "https://www.recaro-cs.com",
            logoUrl = null,
            knownFor = "汽车座椅专家",
            popularProducts = listOf("Monza Nova", "Privia", "Young Sport Hero")
        ),
        BrandInfo(
            name = "Nuna",
            country = "荷兰",
            website = "https://www.nuna.eu",
            logoUrl = null,
            knownFor = "现代简约设计",
            popularProducts = listOf("REBL Plus", "PIVOT X", "Rava")
        ),
        BrandInfo(
            name = "Clek",
            country = "加拿大",
            website = "https://www.clek.com",
            logoUrl = null,
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
            logoUrl = null,
            knownFor = "高端设计",
            popularProducts = listOf("Bee 6", "Fox 5", "Donkey 5")
        ),
        BrandInfo(
            name = "Stokke",
            country = "挪威",
            website = "https://www.stokke.com",
            logoUrl = null,
            knownFor = "北欧设计",
            popularProducts = listOf("Xplory", "Trailz", "Crusi")
        ),
        BrandInfo(
            name = "Silver Cross",
            country = "英国",
            website = "https://www.silvercrossbaby.com",
            logoUrl = null,
            knownFor = "英式皇家品质",
            popularProducts = listOf("Wave", "Reflex", "Jet")
        ),
        BrandInfo(
            name = "UPPAbaby",
            country = "美国",
            website = "https://www.uppababy.com",
            logoUrl = null,
            knownFor = "美式实用",
            popularProducts = listOf("Vista V2", "Cruz V2", "Minu V2")
        ),
        BrandInfo(
            name = "Joie",
            country = "英国",
            website = "https://www.joiebaby.com",
            logoUrl = null,
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
            logoUrl = null,
            knownFor = "Tripp Trapp 系列",
            popularProducts = listOf("Tripp Trapp", "Clikk", "Steps")
        ),
        BrandInfo(
            name = "Cybex",
            country = "德国",
            website = "https://www.cybex-online.com",
            logoUrl = null,
            knownFor = "Lemo 系列",
            popularProducts = listOf("Lemo", "Balios S", "Silver")
        ),
        BrandInfo(
            name = "Chicco",
            country = "意大利",
            website = "https://www.chicco.com",
            logoUrl = null,
            knownFor = "Polly 系列",
            popularProducts = listOf("Polly 2Start", "Pocket Snack", "Hook On")
        ),
        BrandInfo(
            name = "Hauck",
            country = "德国",
            website = "https://www.hauck.de",
            logoUrl = null,
            knownFor = "德国品质",
            popularProducts = listOf("Alpha+", "Beta+", "Nacelle")
        ),
        BrandInfo(
            name = "Safety 1st",
            country = "美国",
            website = "https://www.safety1st.com",
            logoUrl = null,
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
            logoUrl = null,
            knownFor = "性价比高",
            popularProducts = listOf("Gulliver", "Sniglar", "Sundvik")
        ),
        BrandInfo(
            name = "Stokke",
            country = "挪威",
            website = "https://www.stokke.com",
            logoUrl = null,
            knownFor = "Sleepi 系列",
            popularProducts = listOf("Sleepi", "Home", "Crib")
        ),
        BrandInfo(
            name = "BabyBjörn",
            country = "瑞典",
            website = "https://www.babybjorn.com",
            logoUrl = null,
            knownFor = "瑞典设计",
            popularProducts = listOf("Travel Crib Light", "Crib", "Bassinet")
        ),
        BrandInfo(
            name = "Silver Cross",
            country = "英国",
            website = "https://www.silvercrossbaby.com",
            logoUrl = null,
            knownFor = "英式皇家",
            popularProducts = listOf("Cot Bed", "Harmony", "Nostalgia")
        ),
        BrandInfo(
            name = "Hauck",
            country = "德国",
            website = "https://www.hauck.de",
            logoUrl = null,
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
