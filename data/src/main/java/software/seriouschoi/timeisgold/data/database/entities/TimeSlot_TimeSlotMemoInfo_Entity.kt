package software.seriouschoi.timeisgold.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["timeslotId"]),
        Index(value = ["timeslotMemoId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = TimeSlotEntity::class,
            parentColumns = ["id"],
            childColumns = ["timeslotId"],
        ),
        ForeignKey(
            entity = TimeSlotMemoEntity::class,
            parentColumns = ["id"],
            childColumns = ["timeslotMemoId"],
        )
    ]
)
internal data class TimeSlot_TimeSlotMemoInfo_Entity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val timeslotId: Long,
    val timeslotMemoId: Long
)