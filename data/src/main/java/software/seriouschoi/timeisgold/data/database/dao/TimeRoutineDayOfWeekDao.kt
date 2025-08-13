package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekSchema

@Dao
internal abstract class TimeRoutineDayOfWeekDao {

    @Query("SELECT * FROM TimeRoutineDayOfWeekSchema WHERE uuid = :uuid")
    abstract suspend fun get(uuid: String): TimeRoutineDayOfWeekSchema?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(dayOfWeek: TimeRoutineDayOfWeekSchema): Long

    @Delete
    abstract fun delete(dayOfWeekEntity: TimeRoutineDayOfWeekSchema)

    @Query("DELETE FROM TimeRoutineDayOfWeekSchema WHERE timeRoutineId = :timeRoutineId")
    abstract fun delete(timeRoutineId: Long)

}
