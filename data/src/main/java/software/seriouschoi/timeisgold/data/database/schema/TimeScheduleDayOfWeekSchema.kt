package software.seriouschoi.timeisgold.data.database.schema

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.DayOfWeek


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
        )
    ]
)
internal data class TimeScheduleDayOfWeekSchema(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val dayOfWeek: DayOfWeek,
    val uuid: String,
    val timeScheduleId: Long
)
