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
    abstract fun get(uuid: String): TimeRoutineSchema?

    @Update
    abstract fun update(timeRoutineSchema: TimeRoutineSchema)

    @Delete
    abstract fun delete(timeRoutineSchema: TimeRoutineSchema)

    @Query(
        """
            SELECT tr.uuid
            FROM TimeRoutineDayOfWeekSchema trd
            INNER JOIN TimeRoutineSchema tr
            ON trd.timeRoutineId = tr.id
            WHERE trd.dayOfWeek = :dayOfWeek
            ORDER BY tr.createTime DESC
            LIMIT 1
        """
    )
    abstract fun getLatestUuidByDayOfWeekFlow(dayOfWeek: DayOfWeek): Flow<String?>
}
