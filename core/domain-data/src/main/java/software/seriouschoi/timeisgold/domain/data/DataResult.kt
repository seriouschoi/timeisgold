package software.seriouschoi.timeisgold.domain.data

/**
 * Created by jhchoi on 2025. 9. 19.
 * jhchoi
 *
 * 데이터 계층에서 사용할 상태표현 객체.
 */
sealed interface DataResult<out T> {
    data class Success<T>(val value: T) : DataResult<T>
    data class Failure(val error: DataError, val exception: Throwable? = null) : DataResult<Nothing>
}

/**
 * 데이터 계층에서 분리가 완료된 에러 타입.
 */
sealed interface DataError {
    data object Conflict : DataError
    data object NotFound : DataError
    data object Unknown : DataError
}

fun <T> DataResult<T>.asDomainResult(): DomainResult<T> {
    return when (this) {
        is DataResult.Failure -> {
            when (this.error) {
                DataError.Conflict -> DomainResult.Failure(DomainError.Conflict.Data)
                DataError.NotFound -> DomainResult.Failure(DomainError.NotFound.TimeSlot)
                else -> DomainResult.Failure(
                    error = DomainError.Technical.Unknown,
                    exception = this.exception
                )
            }
        }

        is DataResult.Success -> {
            DomainResult.Success(this.value)
        }
    }
}