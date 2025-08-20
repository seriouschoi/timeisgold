package software.seriouschoi.timeisgold.data.database.schema

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    indices = [
        Index(value = ["uuid"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = TimeSlotSchema::class,
            parentColumns = ["id"],
            childColumns = ["timeSlotId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class TimeSlotMemoSchema(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val memo: String,
    val uuid: String,
    val createTime: Long,
    val timeSlotId: Long
)