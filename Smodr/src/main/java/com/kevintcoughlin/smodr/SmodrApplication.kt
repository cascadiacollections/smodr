package com.kevintcoughlin.smodr

import android.app.Application

class SmodrApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        CrashReporter.init(this)
        container = AppContainer(this)
    }
}