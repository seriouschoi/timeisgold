package software.seriouschoi.timeisgold.data.database.schema

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["uuid"], unique = true)
    ]
)
internal data class TimeScheduleSchema(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val uuid: String,
    val title: String,
    val createTime: Long
)
