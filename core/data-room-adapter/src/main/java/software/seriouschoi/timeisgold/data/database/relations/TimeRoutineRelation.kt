package software.seriouschoi.timeisgold.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema

@Deprecated("해당 개념은 DatabaseView로 대체될 예정.")
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