package co.ec.helper

import android.app.Application
import android.content.Context
import co.ec.helper.helpers.ExceptionHelper

open class CnsynApp : Application() {

    companion object {

        private lateinit var instance: CnsynApp

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
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHelper(applicationContext))
    }



}