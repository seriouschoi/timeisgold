package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekEntity
import java.time.DayOfWeek

@Dao
internal abstract class TimeRoutineDayOfWeekDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(dayOfWeek: TimeRoutineDayOfWeekEntity): Long

    @Query(
        """
        DELETE FROM TimeRoutineDayOfWeekEntity
        WHERE dayOfWeek = :dayOfWeek
    """
    )
    abstract fun delete(dayOfWeek: DayOfWeek)

    @Query(
        """
        DELETE FROM TimeRoutineDayOfWeekEntity 
        WHERE timeRoutineId = :timeRoutineId
    """
    )
    abstract fun delete(timeRoutineId: Long)

    @Query(
        """
        SELECT * FROM TimeRoutineDayOfWeekEntity
        WHERE timeRoutineId = :timeRoutineId
    """
    )
    abstract fun watch(timeRoutineId: Long): Flow<List<TimeRoutineDayOfWeekEntity>>

    @Query(
        """
            SELECT * FROM TimeRoutineDayOfWeekEntity
            WHERE dayOfWeek = :dayOfWeek
            ORDER BY id DESC
            LIMIT 1
        """
    )
    abstract fun watchLatest(dayOfWeek: DayOfWeek): Flow<TimeRoutineDayOfWeekEntity?>

    @Query("SELECT * FROM TimeRoutineDayOfWeekEntity")
    abstract fun watchAll(): Flow<List<TimeRoutineDayOfWeekEntity>>

}
