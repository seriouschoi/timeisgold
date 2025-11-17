package software.seriouschoi.timeisgold.domain.data

/**
 * Created by jhchoi on 2025. 9. 2.
 * jhchoi
 *
 * 도메인 계층에서 사용할 상태표현 객체.
 */
sealed class DomainResult<out T> {
    data class Success<T>(val value: T) : DomainResult<T>()
    data class Failure(val error: DomainError, val exception: Throwable? = null) : DomainResult<Nothing>()
}

/**
 * 도메인 계층에서 분리가 완료된 에러 타입.
 */
sealed interface DomainError {
    /**
     * 유효성 검사 실패.
     */
    sealed interface Validation : DomainError {
        data object EmptyTitle : Validation
        data object TitleLength : Validation
        data object NoSelectedDayOfWeek : Validation
    }

    /**
     * 데이터가 충돌하는 경우.
     */
    sealed interface Conflict : DomainError {
        data object DayOfWeek : Conflict
        data object Data : Conflict
        data object Time : Conflict
    }

    /**
     * 데이터가 존재하지 않는 경우.
     */
    sealed interface NotFound : DomainError {
        data object TimeRoutine : NotFound
        data object TimeSlot : NotFound
        data object Data: NotFound
    }

    /**
     * 기타 기술적인 에러.
     */
    sealed interface Technical : DomainError {
        data object Unknown : Technical
    }
}