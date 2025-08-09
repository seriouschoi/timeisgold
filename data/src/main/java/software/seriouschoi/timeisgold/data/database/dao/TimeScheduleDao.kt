package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import software.seriouschoi.timeisgold.data.database.entities.TimeScheduleEntity

@Dao
internal abstract class TimeScheduleDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(entity: TimeScheduleEntity): Long

    @Query("SELECT * FROM TimeScheduleEntity WHERE uuid = :uuid")
    abstract fun get(uuid: String): TimeScheduleEntity?

    @Update
    abstract fun update(timeScheduleEntity: TimeScheduleEntity)

    @Delete
    abstract fun delete(timeScheduleEntity: TimeScheduleEntity)
}
