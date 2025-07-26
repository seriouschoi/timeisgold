package software.seriouschoi.timeisgold.data.database.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotMemoEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeSlot_TimeSlotMemoInfo_Entity

internal data class TimeSlotWithExtrasRelation(
    @Embedded val timeSlot: TimeSlotEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            TimeSlot_TimeSlotMemoInfo_Entity::class,
            parentColumn = "timeslotId",
            entityColumn = "timeslotMemoId"
        )
    )
    val memo: TimeSlotMemoEntity?
)
