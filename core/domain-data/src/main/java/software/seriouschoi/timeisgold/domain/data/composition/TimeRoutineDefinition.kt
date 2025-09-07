package software.seriouschoi.timeisgold.domain.data.composition

import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity

data class TimeRoutineDefinition(
    val timeRoutine: TimeRoutineEntity,
    val dayOfWeeks: Set<TimeRoutineDayOfWeekEntity>
)