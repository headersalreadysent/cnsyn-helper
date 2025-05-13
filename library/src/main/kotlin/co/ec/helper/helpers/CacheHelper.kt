package co.ec.helper.helpers

import android.content.Context
import co.ec.helper.utils.unix
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CacheHelper(context: Context, val shareName: String = "cache") {

    //private cache object
    private val cache = SettingsHelper(context, shareName)

    /**
     * cache change event
     */
    data class CacheChange(var name: String, var value: Any? = null)

    companion object {
        private lateinit var instance: CacheHelper

        fun get(): CacheHelper {
            return instance
        }
    }

    init {
        instance = this
    }

    fun put(name: String, value: String, time: Int = 86400) {
        val cacheString = (unix() + time).toString() + "||" + value
        cache.putString(name, cacheString)
        publish(name,value)
        LogHelper.i("$shareName put-cache: $name => $cacheString")
    }

    fun get(name: String): String? {
        val value = cache.getString(name) ?: return null
        val values = value.split("||")
        val ttl = values[0].toLong() - unix()
        if (ttl < 0) {
            return null
        }
        LogHelper.i("$shareName get-cache: $name($ttl) => ${values[1]}")
        return values[1]
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun publish(key: String, value: Any?) {
        GlobalScope.launch {
            EventBus.publish(CacheChange(key, value))
        }
    }
}