package co.ec.helper.helpers

import android.content.Context
import android.os.Bundle
import co.ec.helper.CnsynApp

object Analytics {

    private val firebaseAnalytics by lazy {
        try {
            val clazz = Class.forName("com.google.firebase.analytics.FirebaseAnalytics")
            val instanceMethod = clazz.getMethod("getInstance", Context::class.java)
            instanceMethod.invoke(null, CnsynApp.context()) // Replace with your Application context
        } catch (e: Exception) {
            null
        }
    }

    /**
     * log event with bundle
     */
    fun logBundle(eventName: String, params: Bundle? = null) {
        if(firebaseAnalytics==null){
            LogHelper.e("Project has no analytics setting")
        } else {
            firebaseAnalytics?.let { analytics ->
                try {
                    val logEventMethod = analytics.javaClass.getMethod(
                        "logEvent", String::class.java, Bundle::class.java
                    )
                    logEventMethod.invoke(analytics, eventName, params)
                } catch (e: Exception) {
                    LogHelper.d("Analytics Error: ${e.message}")
                }
            }
        }
    }

    /**
     * log event with varargs
     */
    fun log(eventName: String, vararg params: Any) {
        val bundle = Bundle()
        for (i in params.indices step 2) {
            val key = params[i] as? String ?: continue
            when (val value = params.getOrNull(i + 1)) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Double -> bundle.putDouble(key, value)
                is Boolean -> bundle.putBoolean(key, value)
            }
        }
        logBundle(eventName, bundle)
    }
}