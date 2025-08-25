package software.seriouschoi.timeisgold.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema

/**
 * Created by jhchoi on 2025. 8. 7.
 * jhchoi
 */
internal data class TimeRoutineDayOfWeekRelation (
    @Embedded val dayOfWeekEntity: TimeRoutineDayOfWeekSchema,
    @Relation(
        parentColumn = "timeRoutineId",
        entityColumn = "id"
    )
    val timeRoutine: TimeRoutineSchema
)