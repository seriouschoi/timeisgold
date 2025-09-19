package software.seriouschoi.timeisgold.domain.data

/**
 * Created by jhchoi on 2025. 9. 19.
 * jhchoi
 */
sealed interface DataResult<out T> {
    data class Success<T>(val value: T) : DataResult<T>
    data class Failure(val error: DataError, val exception: Throwable? = null) : DataResult<Nothing>
}

sealed interface DataError {
    data object Conflict : DataError
    data object NotFound : DataError
    data object Unknown : DataError
}
