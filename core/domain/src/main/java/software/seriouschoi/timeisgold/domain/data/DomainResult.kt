package software.seriouschoi.timeisgold.domain.data

/**
 * Created by jhchoi on 2025. 9. 2.
 * jhchoi
 */
sealed interface DomainError {
    data class Validation(val code: ValidationCode): DomainError
    data class Conflict(val code: ConflictCode): DomainError
    data class NotFound(val code: NotFoundCode): DomainError
    data class Technical(val code: TechCode): DomainError
}

sealed class DomainResult<out T> {
    data class Success<T>(val value: T): DomainResult<T>()
    data class Failure(val error: DomainError): DomainResult<Nothing>()
}

sealed interface ValidationCode {
    sealed interface TimeRoutine: ValidationCode {
        object Title: TimeRoutine
        object DayOfWeekEmpty: TimeRoutine
    }
}
sealed interface ConflictCode {
    sealed interface TimeRoutine: ConflictCode {
        object DayOfWeek: TimeRoutine
        object Data: TimeRoutine
    }
}
sealed interface NotFoundCode {
    object TimeRoutine: NotFoundCode
}
interface TechCode {
    object Data: TechCode
}