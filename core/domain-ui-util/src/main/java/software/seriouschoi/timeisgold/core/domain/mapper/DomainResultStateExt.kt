package software.seriouschoi.timeisgold.core.domain.mapper

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult

/**
 * Created by jhchoi on 2025. 9. 17.
 * jhchoi
 */
class DomainErrorException(val error: DomainError, val from: Throwable? = null) : Exception()

fun <T> DomainResult<T>?.asResultState(): ResultState<T> {
    return when (this) {
        is DomainResult.Failure -> {
            ResultState.Error(
                DomainErrorException(error = this.error, from = this.exception)
            )
        }
        is DomainResult.Success -> ResultState.Success(
            this.value
        )
        null -> ResultState.Loading
    }
}



@Deprecated("use asResultState")
fun <T> Flow<ResultState<DomainResult<T>>>.onlyDomainSuccess(): Flow<T?> =
    this.map { (it as? ResultState.Success)?.data }
        .map { (it as? DomainResult.Success)?.value }

@Deprecated("use asResultState")
fun <T> Flow<ResultState<T>>.onlyResultSuccess(): Flow<T?> =
    this.map { (it as? ResultState.Success)?.data }

@Deprecated("use asResultState")
fun <T> ResultState<T>.onlyResultSuccess(): T? {
    return when (this) {
        is ResultState.Success -> {
            this.data
        }

        else -> null
    }
}

@Deprecated("use asResultState")
fun <T> DomainResult<T>.onlySuccess(): T? {
    return when (this) {
        is DomainResult.Failure -> null
        is DomainResult.Success -> this.value
    }
}

@Deprecated("use asResultState")
fun <T> ResultState<DomainResult<T>>.onlyDomainSuccess(): T? {
    return when (this) {
        is ResultState.Success -> {
            when (val domainResult = this.data) {
                is DomainResult.Failure -> null
                is DomainResult.Success -> domainResult.value
            }
        }

        else -> null
    }
}

@Deprecated("use asResultState")
fun <T> Flow<ResultState<DomainResult<T>>>.onlyDomainResult(): Flow<DomainResult<T>?> {
    return this.map { resultState ->
        resultState.onlyDomainResult()
    }
}

@Deprecated("use asResultState")
fun <T> ResultState<DomainResult<T>>.onlyDomainResult(): DomainResult<T>? {
    return when (this) {
        is ResultState.Error -> DomainResult.Failure(DomainError.Technical.Unknown)
        ResultState.Loading -> null
        is ResultState.Success -> {
            this.data
        }
    }
}