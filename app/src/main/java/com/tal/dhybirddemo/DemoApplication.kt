package com.tal.dhybirddemo

import android.app.Application
import android.util.Log
import com.dahai.dhybird.DHybird
import com.tencent.smtt.sdk.QbSdk


class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initDHybird()
    }


    private fun initDHybird() {
        val cb = object : QbSdk.PreInitCallback {

            override fun onViewInitFinished(arg0: Boolean) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e(DHybird.TAG, "x5Finished: $arg0")
            }

            override fun onCoreInitFinished() {
            }
        }
        //x5内核初始化接口
        QbSdk.initX5Environment(applicationContext, cb)
    }
}