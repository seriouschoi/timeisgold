package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema

@Dao
internal abstract class TimeRoutineDao {

    @Query("SELECT * FROM TimeRoutineSchema WHERE uuid = :uuid")
    abstract fun watch(uuid: String): Flow<TimeRoutineSchema?>

    @Query("SELECT * FROM TimeRoutineSchema WHERE uuid = :uuid")
    abstract suspend fun get(uuid: String): TimeRoutineSchema?

    @Query("SELECT * FROM TimeRoutineSchema WHERE id = :id")
    abstract fun watch(id: Long): Flow<TimeRoutineSchema?>

    @Query("SELECT * FROM TimeRoutineSchema WHERE id = :id")
    abstract suspend fun get(id: Long): TimeRoutineSchema?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(entity: TimeRoutineSchema): Long

    @Update
    abstract fun update(timeRoutineSchema: TimeRoutineSchema): Int

    @Delete
    abstract fun delete(timeRoutineSchema: TimeRoutineSchema): Int
}
