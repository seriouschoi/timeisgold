package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineEntity

@Dao
internal abstract class TimeRoutineDao {

    @Query("SELECT * FROM TimeRoutineEntity WHERE uuid = :uuid")
    abstract fun watch(uuid: String): Flow<TimeRoutineEntity?>

    @Query("SELECT * FROM TimeRoutineEntity WHERE uuid = :uuid")
    abstract suspend fun get(uuid: String): TimeRoutineEntity?

    @Query("SELECT * FROM TimeRoutineEntity WHERE id = :id")
    abstract fun watch(id: Long): Flow<TimeRoutineEntity?>

    @Query("SELECT * FROM TimeRoutineEntity WHERE id = :id")
    abstract suspend fun get(id: Long): TimeRoutineEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(entity: TimeRoutineEntity): Long

    @Update
    abstract fun update(timeRoutineEntity: TimeRoutineEntity): Int

    @Delete
    abstract fun delete(timeRoutineEntity: TimeRoutineEntity): Int
}
