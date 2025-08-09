package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import software.seriouschoi.timeisgold.data.database.entities.TimeScheduleDayOfWeekEntity

@Dao
internal abstract class TimeScheduleDayOfWeekDao {

    @Query("SELECT * FROM TimeScheduleDayOfWeekEntity WHERE uuid = :uuid")
    abstract suspend fun get(uuid: String): TimeScheduleDayOfWeekEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(dayOfWeek: TimeScheduleDayOfWeekEntity): Long

    @Delete
    abstract fun delete(dayOfWeekEntity: TimeScheduleDayOfWeekEntity)

    @Query("DELETE FROM TimeScheduleDayOfWeekEntity WHERE timeScheduleId = :timeScheduleId")
    abstract fun delete(timeScheduleId: Long)

}
