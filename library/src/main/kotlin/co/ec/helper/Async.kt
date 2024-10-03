package co.ec.helper

import android.os.Handler
import kotlin.concurrent.thread

object Async {

    private var defaultErrorHandler: (res: Throwable) -> Unit = { _ -> }

    /**
     * set default handler
     */
    fun setDefaultHandler(errHandler: (res: Throwable) -> Unit = { _ -> }) {
        this.defaultErrorHandler = errHandler
    }

    /**
     * run a job with background thread
     */
    fun <T : Any?> run(
        run: () -> T,
        then: (res: T) -> Unit = { _ -> },
        err: ((res: Throwable) -> Unit)? = null
    ) {
        thread {
            //generate a thread
            val mainHandler = Handler(App.context().mainLooper)
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
                    (err ?: defaultErrorHandler)(error)
                }
                AppLogger.e("Async error", error, "Async")
            }
        }
    }
}