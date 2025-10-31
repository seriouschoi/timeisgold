package software.seriouschoi.timeisgold.data.database.schema

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["createTime"], orders = arrayOf(Index.Order.DESC))
    ]
)
internal data class TimeRoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uuid: String,
    val title: String,
    val createTime: Instant
)
