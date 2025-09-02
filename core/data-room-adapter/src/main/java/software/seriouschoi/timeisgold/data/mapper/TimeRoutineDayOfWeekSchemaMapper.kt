package software.seriouschoi.timeisgold.data.mapper

import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineDayOfWeekEntity

internal fun TimeRoutineDayOfWeekEntity.toTimeRoutineDayOfWeekSchema(
    timeRoutineId: Long,
    id: Long? = null
): TimeRoutineDayOfWeekSchema {
    return TimeRoutineDayOfWeekSchema(
        id = id,
        dayOfWeek = this.dayOfWeek,
        timeRoutineId = timeRoutineId
    )
}

