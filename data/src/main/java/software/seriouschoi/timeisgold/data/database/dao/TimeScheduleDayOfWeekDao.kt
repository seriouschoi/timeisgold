package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import software.seriouschoi.timeisgold.data.database.schema.TimeScheduleDayOfWeekSchema

@Dao
internal abstract class TimeScheduleDayOfWeekDao {

    @Query("SELECT * FROM TimeScheduleDayOfWeekSchema WHERE uuid = :uuid")
    abstract suspend fun get(uuid: String): TimeScheduleDayOfWeekSchema?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(dayOfWeek: TimeScheduleDayOfWeekSchema): Long

    @Delete
    abstract fun delete(dayOfWeekEntity: TimeScheduleDayOfWeekSchema)

    @Query("DELETE FROM TimeScheduleDayOfWeekSchema WHERE timeScheduleId = :timeScheduleId")
    abstract fun delete(timeScheduleId: Long)

}
