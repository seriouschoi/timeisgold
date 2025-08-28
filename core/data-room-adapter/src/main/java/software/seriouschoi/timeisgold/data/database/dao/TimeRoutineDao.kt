package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema
import java.time.DayOfWeek

@Dao
internal abstract class TimeRoutineDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(entity: TimeRoutineSchema): Long

    @Query("SELECT * FROM TimeRoutineSchema WHERE uuid = :uuid")
    abstract fun get(uuid: String): Flow<TimeRoutineSchema?>

    @Update
    abstract fun update(timeRoutineSchema: TimeRoutineSchema)

    @Delete
    abstract fun delete(timeRoutineSchema: TimeRoutineSchema): Int
}
