package software.seriouschoi.timeisgold.domain.data

/**
 * Created by jhchoi on 2025. 9. 2.
 * jhchoi
 */
sealed class DomainResult<out T> {
    data class Success<T>(val value: T) : DomainResult<T>()
    data class Failure(val error: DomainError, val exception: Throwable? = null) : DomainResult<Nothing>()
}

sealed interface DomainError {
    sealed interface Validation : DomainError {
        data object EmptyTitle : Validation
        data object TitleLength : Validation
        data object NoSelectedDayOfWeek : Validation
    }

    sealed interface Conflict : DomainError {
        data object DayOfWeek : Conflict
        data object Data : Conflict
        data object Time : Conflict
    }

    sealed interface NotFound : DomainError {
        data object TimeRoutine : NotFound
        data object TimeSlot : NotFound
    }

    sealed interface Technical : DomainError {
        data object Unknown : Technical
    }
}