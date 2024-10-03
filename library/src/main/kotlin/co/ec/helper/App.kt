package co.ec.helper

import android.app.Application
import android.content.Context

open class App : Application() {

    companion object {

        private lateinit var instance: App

        fun context(): Context {
            return instance.applicationContext
        }

        fun contextCheck(): Context? {
            return if (::instance.isInitialized) instance.applicationContext else null
        }
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        Thread.setDefaultUncaughtExceptionHandler(AppExceptionHandler(applicationContext))

    }

}