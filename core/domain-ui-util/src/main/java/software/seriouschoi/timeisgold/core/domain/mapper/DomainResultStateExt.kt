package software.seriouschoi.timeisgold.core.domain.mapper

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult

/**
 * Created by jhchoi on 2025. 9. 17.
 * jhchoi
 */
fun <T> Flow<ResultState<DomainResult<T>>>.onlySuccess(): Flow<T?> =
    this.map { (it as? ResultState.Success)?.data }
        .map { (it as? DomainResult.Success)?.value }


fun <T> Flow<ResultState<DomainResult<T>>>.onlyDomainResult(): Flow<DomainResult<T>?> {
    return this.map { resultState ->
        resultState.onlyDomainResult()
    }
}

fun <T> ResultState<DomainResult<T>>.onlyDomainResult(): DomainResult<T>? {
    return when (this) {
        is ResultState.Error -> DomainResult.Failure(DomainError.Technical.Unknown)
        ResultState.Loading -> null
        is ResultState.Success -> {
            this.data
        }
    }
}