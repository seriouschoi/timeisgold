package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotMemoEntity

@Dao
internal abstract class TimeSlotMemoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insert(timeSlots: TimeSlotMemoEntity): Long

    @Update
    abstract fun update(timeSlots: TimeSlotMemoEntity)

    @Delete
    abstract fun delete(memo: TimeSlotMemoEntity)

    @Query("SELECT * FROM TimeSlotMemoEntity WHERE uuid = :uuid")
    abstract fun get(uuid: String) : TimeSlotMemoEntity?
}