package software.seriouschoi.timeisgold.domain.data

/**
 * Created by jhchoi on 2025. 9. 2.
 * jhchoi
 */
sealed class DomainResult<out T> {
    data class Success<T>(val value: T) : DomainResult<T>()
    data class Failure(val error: DomainError) : DomainResult<Nothing>()
}

sealed interface DomainError {
    data class Validation(val code: ValidationCode) : DomainError
    data class Conflict(val code: ConflictCode) : DomainError
    data class NotFound(val code: NotFoundCode) : DomainError
    data class Technical(val code: TechCode) : DomainError
}

sealed interface ValidationCode {
    data object Title : ValidationCode
    data object NoSelectedDayOfWeek : ValidationCode
}

sealed interface ConflictCode {
    data object DayOfWeek : ConflictCode
    data object Data : ConflictCode
}

sealed interface NotFoundCode {
    data object TimeRoutine : NotFoundCode
}

interface TechCode {
    data object Data : TechCode
}