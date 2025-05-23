package co.ec.helper.helpers

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.core.content.edit

open class SettingsHelper(context: Context, preferencesName: String = "AppSettings") {

    /**
     * settings change event
     */
    data class SettingsChange(var name: String, var value: Any? = null)

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

    companion object {
        private lateinit var instance: SettingsHelper

        fun get(): SettingsHelper {
            return instance
        }
    }

    init {
        instance = this
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun publish(key: String, value: Any?) {
        GlobalScope.launch {
            EventBus.publish(SettingsChange(key, value))
        }
    }

    // Store String value
    open fun putString(key: String, value: String) {
        sharedPreferences.edit() { putString(key, value) }
        publish(key, value)
    }

    // Retrieve String value
    open fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    // Store Int value
    open fun putInt(key: String, value: Int) {
        sharedPreferences.edit() { putInt(key, value) }
        publish(key, value)
    }

    // Retrieve Int value
    open fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    // Store Boolean value
    open fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit() { putBoolean(key, value) }
        publish(key, value)
    }

    // Retrieve Boolean value
    open fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // Store Float value
    open fun putFloat(key: String, value: Float) {
        sharedPreferences.edit() { putFloat(key, value) }
        publish(key, value)
    }

    // Retrieve Float value
    open fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    // Remove a setting
    open fun remove(key: String) {
        sharedPreferences.edit() { remove(key) }
        publish(key, null)
    }

    /**
     * purge all settings
     */
    open fun purge(key: String) {
        sharedPreferences.all.forEach {
            if (it.key.contains(key)) {
                remove(it.key)
            }
        }
    }

    /**
     * get all keys
     */
    fun keys(): List<String> {
        return sharedPreferences.all.keys.toList()
    }

    /**
     * get all values
     */
    fun all(): Map<String, Any?> {
        val values= mutableMapOf<String,Any>()
        sharedPreferences.all.forEach {
            it.value?.let { value->
                values[it.key]=value
            }
        }
        return values
    }

    // Clear all settings
    open fun clear() {
        sharedPreferences.edit(true) { clear() }
    }
}