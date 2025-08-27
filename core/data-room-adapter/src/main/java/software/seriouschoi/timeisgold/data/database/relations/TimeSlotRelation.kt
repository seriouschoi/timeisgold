package software.seriouschoi.timeisgold.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotMemoSchema

@Deprecated("해당 개념은 DatabaseView로 대체될 예정.")
internal data class TimeSlotRelation(
    @Embedded val timeSlot: TimeSlotSchema,
    @Relation(
        parentColumn = "id",
        entityColumn = "timeSlotId",
    )
    val memo: TimeSlotMemoSchema?
)
