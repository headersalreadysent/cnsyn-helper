package co.ec.helper.utils

import android.os.Handler
import co.ec.helper.CnsynApp
import co.ec.helper.helpers.LogHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread


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


/**
 * run a job with background thread
 */
fun <T : Any?> runOld(
    run: () -> T,
    then: (res: T) -> Unit = { _ -> },
    err: (res: Throwable) -> Unit = { }
) {
    thread {
        //generate a thread
        val mainHandler = Handler(CnsynApp.context().mainLooper)
        try {
            //run method ang get result
            val result: T = run()
            mainHandler.post {
                //send to main thread with result
                then(result)
            }
        } catch (error: Throwable) {
            mainHandler.post {
                //send to main thread with error
                err(error)
            }
            LogHelper.e("Async error", error)
        }
    }
}