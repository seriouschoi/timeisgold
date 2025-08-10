package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import software.seriouschoi.timeisgold.data.database.schema.TimeScheduleSchema

@Dao
internal abstract class TimeScheduleDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(entity: TimeScheduleSchema): Long

    @Query("SELECT * FROM TimeScheduleSchema WHERE uuid = :uuid")
    abstract fun get(uuid: String): TimeScheduleSchema?

    @Update
    abstract fun update(timeScheduleSchema: TimeScheduleSchema)

    @Delete
    abstract fun delete(timeScheduleSchema: TimeScheduleSchema)
}
