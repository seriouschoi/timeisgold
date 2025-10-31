package software.seriouschoi.timeisgold.data.database.schema

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalTime

@Entity(
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["timeRoutineId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = TimeRoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["timeRoutineId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
internal data class TimeSlotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val title: String,
    val uuid: String,
    val createTime: Instant,
    val timeRoutineId: Long
)
