package software.seriouschoi.timeisgold.core.common.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart

/**
 * UI계층에서 사용할 상태 표현 객체.
 */
sealed class ResultState<out T> {
    object Loading : ResultState<Nothing>()
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val throwable: Throwable) : ResultState<Nothing>()
}

/**
 * flow의 생명주기를 상태로 전환.
 */
fun <T> Flow<ResultState<T>>.withResultStateLifecycle(): Flow<ResultState<T>> {
    return this
        .onStart { emit(ResultState.Loading) }
        .catch { e -> emit(ResultState.Error(e)) }
}