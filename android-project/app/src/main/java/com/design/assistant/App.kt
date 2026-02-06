package com.design.assistant

import android.app.Application
import androidx.room.Room
import com.design.assistant.database.gps028.Gps028Database
import com.design.assistant.database.travel.childseat.aus.AsNzs1754Database
import com.design.assistant.database.travel.childseat.canada.Cmvss213Database
import com.design.assistant.database.travel.childseat.eu.EceR129Database
import com.design.assistant.database.travel.childseat.us.Fmvss213Database
import com.design.assistant.repository.MultiStandardDesignRepository
import com.design.assistant.repository.gps028.Gps028Repository
import com.design.assistant.repository.travel.ChildSeatRepository
import com.design.assistant.viewmodel.DesignGenerateVM
import com.design.assistant.viewmodel.ProductStandardSelectVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 应用入口类（专业工程师版）
 * 初始化所有数据库/Repository/VM，原有逻辑不变，仅适配专业版
 */
class DesignAssistantApp : Application() {
    // 数据库实例（物理隔离，专业版新增字段后无需修改）
    lateinit var gps028Db: Gps028Database
    lateinit var eceR129Db: EceR129Database
    lateinit var cmvss213Db: Cmvss213Database
    lateinit var fmvss213Db: Fmvss213Database
    lateinit var asNzs1754Db: AsNzs1754Database
    // 其他产品数据库（婴儿推车/高脚椅/儿童床）按原有逻辑初始化...

    // Repository实例（专业版）
    lateinit var gps028Repo: Gps028Repository
    lateinit var childSeatRepo: ChildSeatRepository
    lateinit var multiStandardRepo: MultiStandardDesignRepository

    // VM实例（专业版）
    lateinit var productSelectVM: ProductStandardSelectVM
    lateinit var designGenerateVM: DesignGenerateVM

    override fun onCreate() {
        super.onCreate()
        initDatabases()
        initRepositories()
        initViewModels()
        initDefaultDatabaseData()
    }

    /** 初始化所有数据库（Room），物理隔离，专业版无需修改 */
    private fun initDatabases() {
        gps028Db = Room.databaseBuilder(this, Gps028Database::class.java, Gps028Database.DATABASE_NAME)
            .fallbackToDestructiveMigration() // 开发阶段用，正式版需添加迁移策略
            .build()

        eceR129Db = Room.databaseBuilder(this, EceR129Database::class.java, EceR129Database.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

        cmvss213Db = Room.databaseBuilder(this, Cmvss213Database::class.java, Cmvss213Database.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

        fmvss213Db = Room.databaseBuilder(this, Fmvss213Database::class.java, Fmvss213Database.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

        asNzs1754Db = Room.databaseBuilder(this, AsNzs1754Database::class.java, AsNzs1754Database.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

        // 其他产品数据库（婴儿推车/高脚椅/儿童床）初始化...
    }

    /** 初始化所有Repository（专业版） */
    private fun initRepositories() {
        gps028Repo = Gps028Repository(gps028Db)
        childSeatRepo = ChildSeatRepository(
            eceR129Db = eceR129Db,
            fmvss213Db = fmvss213Db,
            asNzs1754Db = asNzs1754Db,
            cmvss213Db = cmvss213Db,
            gps028Repo = gps028Repo
        )
        multiStandardRepo = MultiStandardDesignRepository(
            childSeatRepo = childSeatRepo,
            babyStrollerRepo = /* 婴儿推车专业版仓库 */,
            highChairRepo = /* 高脚椅专业版仓库 */,
            childBedRepo = /* 儿童床专业版仓库 */
        )
    }

    /** 初始化所有VM（专业版） */
    private fun initViewModels() {
        productSelectVM = ProductStandardSelectVM()
        designGenerateVM = DesignGenerateVM(
            selectVM = productSelectVM,
            multiStandardRepo = multiStandardRepo
        )
    }

    /** 初始化数据库默认数据（GPS028-2023 + 各标准专业数据） */
    private fun initDefaultDatabaseData() {
        CoroutineScope(Dispatchers.IO).launch {
            gps028Repo.initDefaultData() // GPS028-2023专业数据
            // 各标准默认数据初始化（ECE/CMVSS/FMVSS/AS-NZS）
            // childSeatRepo.initEceDefaultData()
            // childSeatRepo.initCmvssDefaultData()
        }
    }
}
