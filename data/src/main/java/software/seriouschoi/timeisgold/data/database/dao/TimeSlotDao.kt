package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotEntity

@Dao
internal abstract class TimeSlotDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insert(timeSlots: TimeSlotEntity): Long

    @Update
    abstract fun update( timeSlots: TimeSlotEntity)

    @Delete
    abstract fun delete(entity: TimeSlotEntity)

    @Query("SELECT * FROM TimeSlotEntity")
    abstract fun getAll(): List<TimeSlotEntity>

    @Query("SELECT id FROM TimeSlotEntity WHERE uuid = :uuid")
    abstract fun getId(uuid: String) : Long?


}