package software.seriouschoi.timeisgold.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema

internal data class TimeRoutineWithDayOfWeeks(
    @Embedded val timeRoutine: TimeRoutineSchema,
    @Relation(
        parentColumn = "id",
        entityColumn = "timeRoutineId",
    )
    val dayOfWeeks: List<TimeRoutineDayOfWeekSchema>
)