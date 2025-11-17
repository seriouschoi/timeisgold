package software.seriouschoi.timeisgold.core.domain.mapper

import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult

/**
 * Created by jhchoi on 2025. 9. 17.
 * jhchoi
 */
data class DomainErrorException(val error: DomainError, val from: Throwable? = null) : Exception() {
    override val message: String? = "$error, from=$from"
}

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

fun ResultState.Error.asDomainError(): DomainError {
    return (this.throwable as? DomainErrorException)?.error ?: DomainError.Technical.Unknown
}