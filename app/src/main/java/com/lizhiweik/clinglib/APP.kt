package com.lizhiweik.clinglib

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import me.shetj.base.s

class APP : Application()  {

    override fun onCreate() {
        super.onCreate()
        s.init(this,BuildConfig.DEBUG )
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}