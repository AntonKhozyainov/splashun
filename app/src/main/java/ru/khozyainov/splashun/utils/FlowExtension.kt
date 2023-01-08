package ru.khozyainov.splashun.utils

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

inline fun <T> Flow<T>.launchAndCollect(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        collect {
            action(it)
        }
    }
}

inline fun <T> Flow<T>.launchAndCollectLatest(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        collectLatest {
            action(it)
        }
    }
}

inline fun <T> Flow<T>.launchAndCollectForActivity(
    owner: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.coroutineScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        collect {
            action(it)
        }
    }
}

fun SearchView.searchChangeFlow(): Flow<String>{
    return callbackFlow {
        val searchWatcher = object : SearchView.OnQueryTextListener, View.OnFocusChangeListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                trySendBlocking(newText.orEmpty())
                return true
            }

            override fun onFocusChange(v: View?, hasFocus: Boolean) {}

        }

        this@searchChangeFlow.setOnQueryTextListener(searchWatcher)

        awaitClose {
            this@searchChangeFlow.setOnQueryTextListener(null)
        }
    }
}

/**
 * Emits the previous values (`null` if there is no previous values) along with the current one.
 * For example:
 * - origin flow:
 *   ```
 *   [
 *     "a",
 *     "b",
 *     "c",
 *     "d"
 *   ]
 *   ```
 * - resulting flow (count = 2):
 *   ```
 *   [
 *     (null, null)
 *     (null, "a"),
 *     ("a",  "b"),
 *     ("b",  "c"),
 *     ("c",  "d")
 *   ]
 *   ```
 */
@ExperimentalCoroutinesApi
fun <T> Flow<T>.simpleScan(count: Int): Flow<List<T?>> {
    val items = List<T?>(count) { null }
    return this.scan(items) { previous, value -> previous.drop(1) + value }
}