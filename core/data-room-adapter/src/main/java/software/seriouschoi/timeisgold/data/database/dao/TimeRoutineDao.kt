package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineSchema
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity

@Dao
internal abstract class TimeRoutineDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(entity: TimeRoutineSchema): Long

    @Query("SELECT * FROM TimeRoutineSchema WHERE uuid = :uuid")
    abstract fun observe(uuid: String): Flow<TimeRoutineSchema?>

    suspend fun get(uuid: String): TimeRoutineSchema? {
        return observe(uuid).first()
    }

    @Update
    abstract fun update(timeRoutineSchema: TimeRoutineSchema)

    suspend fun upsert(timeRoutine: TimeRoutineEntity): Long  {
        // TODO: jhchoi 2025. 10. 28. TimeRoutineEntity 대신 schema로..
        val routineId = get(timeRoutine.uuid)?.id
        return timeRoutine.toTimeRoutineSchema(routineId).let {
            if (it.id == null) add(it)
            else {
                update(it)
                it.id
            }
        }
    }

    @Delete
    abstract fun delete(timeRoutineSchema: TimeRoutineSchema): Int
}
