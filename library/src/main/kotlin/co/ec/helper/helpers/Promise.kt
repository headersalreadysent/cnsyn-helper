package co.ec.helper.helpers

import kotlinx.coroutines.*

class Promise<T>(executor: (resolve: (T) -> Unit, reject: (e:Throwable) -> Unit) -> Unit) {
    private val deferred = CompletableDeferred<T>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                executor(
                    { value -> deferred.complete(value) },
                    { error -> deferred.completeExceptionally(error) }
                )
            } catch (e: Throwable) {
                deferred.completeExceptionally(e)
            }
        }
    }

    fun then(onSuccess: (T) -> Unit): Promise<T> {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                onSuccess(deferred.await())
            } catch (_: Throwable) { }
        }
        return this
    }

    fun catch(onError: (Throwable) -> Unit): Promise<T> {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                deferred.await()
            } catch (e: Throwable) {
                onError(e)
            }
        }
        return this
    }
}
