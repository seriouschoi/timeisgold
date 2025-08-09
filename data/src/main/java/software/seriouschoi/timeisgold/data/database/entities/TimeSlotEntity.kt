package software.seriouschoi.timeisgold.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    indices = [
        Index(value = ["uuid"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = TimeScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["timeScheduleId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
internal data class TimeSlotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val title: String,
    val uuid: String,
    val createTime: Long,
    val timeScheduleId: Long
)
