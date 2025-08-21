package software.seriouschoi.timeisgold.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema

internal data class TimeRoutineRelation(
    @Embedded val timeRoutine: TimeRoutineSchema,
    @Relation(
        parentColumn = "id",
        entityColumn = "timeRoutineId",
    )
    val timeSlots: List<TimeSlotSchema>,

    @Relation(
        parentColumn = "id",
        entityColumn = "timeRoutineId",
    )
    val dayOfWeeks: List<TimeRoutineDayOfWeekSchema>
)