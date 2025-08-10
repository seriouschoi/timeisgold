package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotMemoSchema

@Dao
internal abstract class TimeSlotMemoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insert(timeSlots: TimeSlotMemoSchema): Long

    @Update
    abstract fun update(timeSlots: TimeSlotMemoSchema)

    @Delete
    abstract fun delete(memo: TimeSlotMemoSchema)

    @Query("SELECT * FROM TimeSlotMemoSchema WHERE uuid = :uuid")
    abstract fun get(uuid: String) : TimeSlotMemoSchema?
}