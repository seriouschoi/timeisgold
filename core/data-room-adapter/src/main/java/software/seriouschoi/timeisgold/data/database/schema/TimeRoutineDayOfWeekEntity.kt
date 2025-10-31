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
            entity = TimeRoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["timeRoutineId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class TimeRoutineDayOfWeekEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: DayOfWeek,
    val timeRoutineId: Long
)
