package software.seriouschoi.timeisgold.core.common.ui

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class ResultState<out T> {
    object Loading : ResultState<Nothing>()
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val throwable: Throwable) : ResultState<Nothing>()
}

fun <T> Flow<T>.asResultState(): Flow<ResultState<T>> {
    return this.map<T, ResultState<T>> {
        ResultState.Success(it)
    }
        .onStart { emit(ResultState.Loading) }
        .catch { e -> emit(ResultState.Error(e)) }
}

fun <T> flowResultState(block: suspend () -> T): Flow<ResultState<T>> {
    return flow {
        emit(ResultState.Loading)
        try {
            val result = block.invoke()
            emit(ResultState.Success(result))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(ResultState.Error(e))
        }
    }
}