package software.seriouschoi.timeisgold.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotMemoEntity

internal data class TimeSlotRelation(
    @Embedded val timeSlot: TimeSlotEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "timeSlotId",
    )
    val memo: TimeSlotMemoEntity?
)
