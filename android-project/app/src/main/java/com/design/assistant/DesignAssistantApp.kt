package com.design.assistant

import android.app.Application
import android.util.Log

/**
 * 儿童产品设计助手 Application 类
 * 用于初始化全局资源和数据库
 */
class DesignAssistantApp : Application() {
    companion object {
        private const val TAG = "DesignAssistantApp"
        lateinit var instance: DesignAssistantApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.i(TAG, "儿童产品设计助手应用初始化")
    }
}
