package com.hengda.shzkjg.m.base

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadHelper
import com.tencent.bugly.crashreport.CrashReport
import okhttp3.OkHttpClient
import java.net.Proxy
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * Created by lenovo on 2017/8/8.
 */
class App : MultiDexApplication() {
    companion object {
        var instance: App by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CrashReport.initCrashReport(getApplicationContext(), "58a00b358a", true)

//        if (LeakCanary.isInAnalyzerProcess(instance)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(instance)

        FileDownloader.init(instance) {
            val builder = OkHttpClient.Builder()
            builder.connectTimeout(5000, TimeUnit.MILLISECONDS)
            builder.proxy(Proxy.NO_PROXY)
            builder.build()
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}