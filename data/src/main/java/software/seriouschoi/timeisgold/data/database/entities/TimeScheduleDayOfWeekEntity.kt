package software.seriouschoi.timeisgold.data.database.entities

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
            entity = TimeRoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["timeRoutineId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class TimeRoutineDayOfWeekEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val dayOfWeek: DayOfWeek,
    val uuid: String,
    val timeRoutineId: Long
)
