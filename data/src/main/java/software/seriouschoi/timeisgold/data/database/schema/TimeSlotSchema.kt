package software.seriouschoi.timeisgold.data.database.schema

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["timeRoutineId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = TimeRoutineSchema::class,
            parentColumns = ["id"],
            childColumns = ["timeRoutineId"],
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
    val timeRoutineId: Long
)
