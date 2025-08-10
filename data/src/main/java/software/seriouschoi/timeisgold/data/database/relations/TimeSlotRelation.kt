package software.seriouschoi.timeisgold.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotMemoSchema

internal data class TimeSlotRelation(
    @Embedded val timeSlot: TimeSlotSchema,
    @Relation(
        parentColumn = "id",
        entityColumn = "timeSlotId",
    )
    val memo: TimeSlotMemoSchema?
)
