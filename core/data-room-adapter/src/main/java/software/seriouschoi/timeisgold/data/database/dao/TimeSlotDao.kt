package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema

@Dao
internal abstract class TimeSlotDao {
    @Deprecated("use upsert")
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insert(timeSlot: TimeSlotSchema): Long

    @Deprecated("use upsert")
    @Update
    abstract fun update(timeSlots: TimeSlotSchema)

    @Upsert
    abstract fun upsert(timeSlot: TimeSlotSchema)

    @Query("""
        DELETE FROM TimeSlotSchema 
        WHERE uuid = :slotUuid
    """)
    abstract fun delete(slotUuid: String)

    @Query("""
        SELECT * FROM TimeSlotSchema WHERE uuid = :timeslotUuid
    """)
    abstract fun get(timeslotUuid: String): Flow<TimeSlotSchema?>
}