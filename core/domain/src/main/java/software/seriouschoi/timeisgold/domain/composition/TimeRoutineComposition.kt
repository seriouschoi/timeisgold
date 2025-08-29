package software.seriouschoi.timeisgold.domain.composition

import software.seriouschoi.timeisgold.domain.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineEntity

data class TimeRoutineComposition(
    val timeRoutine: TimeRoutineEntity,
    val timeSlots: List<TimeSlotEntity>,
    val dayOfWeeks: Set<TimeRoutineDayOfWeekEntity>
)