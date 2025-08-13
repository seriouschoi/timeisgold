package software.seriouschoi.timeisgold.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["uuid"], unique = true)
    ]
)
internal data class TimeRoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val uuid: String,
    val title: String,
    val createTime: Long
)
