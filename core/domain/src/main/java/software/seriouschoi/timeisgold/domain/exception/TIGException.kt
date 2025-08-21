package software.seriouschoi.timeisgold.domain.exception

import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotData
import java.time.DayOfWeek

sealed class TIGException : Exception() {
    data class RoutineConflict(
        val conflictDays: Set<DayOfWeek>
    ) : TIGException()

    class TimeRoutineNotFound(val timeRoutineUuid: String) : TIGException()
    class TimeSlotConflict(val timeSlotDataForAdd: TimeSlotData) : TIGException()

}
