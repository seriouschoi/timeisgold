package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekSchema
import java.time.DayOfWeek

@Dao
internal abstract class TimeRoutineDayOfWeekDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(dayOfWeek: TimeRoutineDayOfWeekSchema): Long

    @Query(
        """
        DELETE FROM TimeRoutineDayOfWeekSchema
        WHERE dayOfWeek = :dayOfWeek
    """
    )
    abstract fun delete(dayOfWeek: DayOfWeek)

    @Query(
        """
        DELETE FROM TimeRoutineDayOfWeekSchema 
        WHERE timeRoutineId = :timeRoutineId
    """
    )
    abstract fun delete(timeRoutineId: Long)

    @Query(
        """
        SELECT * FROM TimeRoutineDayOfWeekSchema
        WHERE timeRoutineId = :timeRoutineId
    """
    )
    abstract fun watch(timeRoutineId: Long): Flow<List<TimeRoutineDayOfWeekSchema>>

    @Query(
        """
            SELECT * FROM TimeRoutineDayOfWeekSchema
            WHERE dayOfWeek = :dayOfWeek
            ORDER BY id DESC
            LIMIT 1
        """
    )
    abstract fun watchLatest(dayOfWeek: DayOfWeek): Flow<TimeRoutineDayOfWeekSchema?>

    @Query("SELECT * FROM TimeRoutineDayOfWeekSchema")
    abstract fun watchAll(): Flow<List<TimeRoutineDayOfWeekSchema>>

}
