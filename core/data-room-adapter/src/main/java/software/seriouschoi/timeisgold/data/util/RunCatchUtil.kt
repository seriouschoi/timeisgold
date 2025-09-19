package software.seriouschoi.timeisgold.data.util

import android.database.sqlite.SQLiteConstraintException
import software.seriouschoi.timeisgold.domain.data.DataError
import software.seriouschoi.timeisgold.domain.data.DataResult
import timber.log.Timber

fun <T> Result<T>.asDataResult(): DataResult<T> =
    fold(
        onSuccess = { DataResult.Success(it) },
        onFailure = { DataResult.Failure(it.asDataError(), it) }
    )

private fun Throwable.asDataError(): DataError = when (this) {
    is SQLiteConstraintException -> DataError.Conflict
    else -> {
        Timber.d("error. $this")
        DataError.Unknown
    }
}
