package software.seriouschoi.timeisgold.domain.exception

import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import java.time.DayOfWeek

sealed class TIGException : Exception() {
    // TODO: jhchoi 2025. 9. 2. 도메인 예외는 예상 가능한 실패이므로, throw하지 않는다.
    data class RoutineConflict(
        val conflictDays: Set<DayOfWeek>
    ) : TIGException()

    class TimeRoutineNotFound(val timeRoutineUuid: String) : TIGException()
    class TimeSlotConflict(val timeSlotDataForAdd: TimeSlotEntity) : TIGException()
    class EmptyDayOfWeeks : TIGException()
    class EmptyTitle : TIGException()
}
