package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema

@Dao
internal abstract class TimeRoutineDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(entity: TimeRoutineSchema): Long

    @Query("SELECT * FROM TimeRoutineSchema WHERE uuid = :uuid")
    abstract fun observe(uuid: String): Flow<TimeRoutineSchema?>

    suspend fun get(uuid: String) : TimeRoutineSchema? {
        return observe(uuid).first()
    }

    @Update
    abstract fun update(timeRoutineSchema: TimeRoutineSchema)

    @Delete
    abstract fun delete(timeRoutineSchema: TimeRoutineSchema): Int
}
