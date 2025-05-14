package co.ec.helper.utils

import co.ec.helper.helpers.LogHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun <T : Any?> asyncRun(
    run: suspend () -> T,
    then: (T) -> Unit = {},
    err: (Throwable) -> Unit = {}
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val result = run()
            withContext(Dispatchers.Main) { then(result) }
        } catch (error: Throwable) {
            LogHelper.e("Async error ${error.message}", error)
            withContext(Dispatchers.Main) { err(error) }
        }
    }
}