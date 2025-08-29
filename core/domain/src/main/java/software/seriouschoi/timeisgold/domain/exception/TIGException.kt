package software.seriouschoi.timeisgold.domain.exception

import software.seriouschoi.timeisgold.domain.entities.TimeSlotEntity
import java.time.DayOfWeek

sealed class TIGException : Exception() {
    data class RoutineConflict(
        val conflictDays: Set<DayOfWeek>
    ) : TIGException()

    class TimeRoutineNotFound(val timeRoutineUuid: String) : TIGException()
    class TimeSlotConflict(val timeSlotDataForAdd: TimeSlotEntity) : TIGException()

}
