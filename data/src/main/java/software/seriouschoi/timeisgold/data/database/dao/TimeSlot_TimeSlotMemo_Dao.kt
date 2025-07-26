package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import software.seriouschoi.timeisgold.data.database.entities.TimeSlot_TimeSlotMemoInfo_Entity

@Dao
internal abstract class TimeSlot_TimeSlotMemo_Dao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insert(timeSlotJunction: TimeSlot_TimeSlotMemoInfo_Entity): Long

    @Query("""
        SELECT * FROM TimeSlot_TimeSlotMemoInfo_Entity
        WHERE timeslotId = :timeslotId AND timeslotMemoId = :timeslotMemoId
        """)
    abstract fun get(timeslotId: Long, timeslotMemoId: Long): TimeSlot_TimeSlotMemoInfo_Entity?

    @Delete
    abstract fun delete(junction: TimeSlot_TimeSlotMemoInfo_Entity)
}