package software.seriouschoi.timeisgold.domain.data.composition

import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity

data class TimeRoutineComposition(
    val timeRoutine: TimeRoutineEntity,
    val timeSlots: List<TimeSlotEntity>,
    val dayOfWeeks: Set<TimeRoutineDayOfWeekEntity>
)