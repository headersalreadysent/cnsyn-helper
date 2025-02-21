package co.ec.helper.helpers

import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.coroutines.coroutineContext

object EventBus {

    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()

    /**
     * publish
     */
    suspend fun publish(event: Any) {
        _events.emit(event)
    }

    /**
     * subscribe the event
     */
    suspend inline fun <reified T> subscribe(crossinline onEvent: (T) -> Unit) {
        events.filterIsInstance<T>()
            .collectLatest { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }
}