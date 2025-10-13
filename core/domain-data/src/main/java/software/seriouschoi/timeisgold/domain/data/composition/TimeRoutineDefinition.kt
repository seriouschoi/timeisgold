package software.seriouschoi.timeisgold.domain.data.composition

import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity

@Deprecated("이 단위로 저장할 일이 없어짐.")
data class TimeRoutineDefinition(
    val timeRoutine: TimeRoutineEntity,
    val dayOfWeeks: Set<TimeRoutineDayOfWeekEntity>
)