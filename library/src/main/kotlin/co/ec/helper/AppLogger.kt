package co.ec.helper

import android.util.Log
import co.ec.helper.utils.dateString
import co.ec.helper.utils.unix

object AppLogger {

    /**
     * tag helper
     */
    private var defaultTag = "TagHelper"

    /**
     * set tag as string
     */
    private fun setTag(tag: String): AppLogger {
        defaultTag = tag
        return this
    }

    private fun getTag(customTag: String?): String {
        return if (customTag.isNullOrEmpty()) {
            defaultTag
        } else {
            "$defaultTag - $customTag"
        }
    }

    /**
     * append to log file if custom tag set
     */
    private fun appendToLog(customTag: String? = null, message: String) {
        if (!customTag.isNullOrEmpty()) {
            AppExceptionHandler.appendLog("${unix().dateString()}#$customTag#$message")
        }
    }

    fun d(message: String, customTag: String? = null): AppLogger {
        Log.d(getTag(customTag), message)
        appendToLog(customTag, message)
        return this
    }

    fun e(message: String, throwable: Throwable? = null, customTag: String? = null): AppLogger {
        Log.e(getTag(customTag), message, throwable)
        appendToLog(customTag, message)
        return this
    }

    fun i(message: String, customTag: String? = null): AppLogger {
        Log.i(getTag(customTag), message)
        appendToLog(customTag, message)
        return this
    }

    fun w(message: String, customTag: String? = null): AppLogger {
        Log.w(getTag(customTag), message)
        appendToLog(customTag, message)
        return this
    }

    fun v(message: String, customTag: String? = null): AppLogger {
        Log.v(getTag(customTag), message)
        appendToLog(customTag, message)
        return this
    }
}

