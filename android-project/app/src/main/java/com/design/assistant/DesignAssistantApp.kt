package com.design.assistant

import android.app.Application
import android.content.Context
import android.util.Log

/**
 * 应用程序类
 * 用于初始化全局配置和资源
 */
class DesignAssistantApp : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            Log.d(TAG, "应用程序初始化开始")
            // 初始化全局配置
            // 这里可以添加初始化代码，如：
            // - 初始化崩溃报告
            // - 初始化第三方SDK
            // - 初始化数据库
            Log.d(TAG, "应用程序初始化完成")
        } catch (e: Exception) {
            Log.e(TAG, "应用程序初始化失败", e)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        try {
            // 在这里可以添加一些早期的初始化代码
        } catch (e: Exception) {
            Log.e(TAG, "attachBaseContext 失败", e)
        }
    }

    companion object {
        private const val TAG = "DesignAssistantApp"
    }
}
