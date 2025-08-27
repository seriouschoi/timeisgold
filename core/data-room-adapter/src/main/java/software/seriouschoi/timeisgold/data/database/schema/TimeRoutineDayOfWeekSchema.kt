package software.seriouschoi.timeisgold.data.database.schema

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.DayOfWeek


@Entity(
    indices = [
        Index(value = ["timeRoutineId", "dayOfWeek"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = TimeRoutineSchema::class,
            parentColumns = ["id"],
            childColumns = ["timeRoutineId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class TimeRoutineDayOfWeekSchema(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val dayOfWeek: DayOfWeek,
    val timeRoutineId: Long
)
