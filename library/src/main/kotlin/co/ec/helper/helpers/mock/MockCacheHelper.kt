package co.ec.helper.helpers.mock

import android.content.Context
import co.ec.helper.helpers.CacheHelper

class MockCacheHelper(context: Context) : CacheHelper(context) {


    override fun put(name: String, value: String, time: Int) {
    }

    override fun get(name: String): String? {
        return null
    }
}
