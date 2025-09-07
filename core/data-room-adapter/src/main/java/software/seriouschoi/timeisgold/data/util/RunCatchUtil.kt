package software.seriouschoi.timeisgold.data.util

import android.database.sqlite.SQLiteConstraintException
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import timber.log.Timber

/**
 * 모듈레벨의 오류 분기 함수.
 */
fun Throwable.toDomainError(): DomainError = when (this) {
    is SQLiteConstraintException -> DomainError.Conflict.Data
    else -> {
        Timber.d("error. $this")
        DomainError.Technical.Unknown
    }
}

/**
 * 모듈 레벨의 결과 분기.
 */
fun <T> Result<T>.toDomainResult(): DomainResult<T> =
    fold(
        onSuccess = { DomainResult.Success(it) },
        onFailure = { DomainResult.Failure(it.toDomainError(), it) }
    )