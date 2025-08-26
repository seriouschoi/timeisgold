package software.seriouschoi.timeisgold.data.database.schema

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["createTime"], orders = arrayOf(Index.Order.DESC))
    ]
)
internal data class TimeRoutineSchema(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val uuid: String,
    val title: String,
    val createTime: Long
)
