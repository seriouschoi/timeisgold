package software.seriouschoi.timeisgold.data.database.schema

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
            entity = TimeScheduleSchema::class,
            parentColumns = ["id"],
            childColumns = ["timeScheduleId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
internal data class TimeSlotSchema(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val title: String,
    val uuid: String,
    val createTime: Long,
    val timeScheduleId: Long
)
