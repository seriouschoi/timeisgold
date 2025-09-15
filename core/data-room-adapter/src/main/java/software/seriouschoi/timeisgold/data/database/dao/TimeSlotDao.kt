package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema

@Dao
internal abstract class TimeSlotDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insert(timeSlot: TimeSlotSchema): Long

    @Update
    abstract fun update(timeSlots: TimeSlotSchema)

    suspend fun upsert(timeSlot: TimeSlotSchema): Long {
        val slotId = get(timeSlot.uuid)?.id
        return if(slotId == null) { insert(timeSlot) }
        else {
            update(timeSlot)
            slotId
        }
    }

    @Query("""
        DELETE FROM TimeSlotSchema 
        WHERE uuid = :slotUuid
    """)
    abstract fun delete(slotUuid: String)

    @Query("""
        SELECT * FROM TimeSlotSchema WHERE uuid = :timeslotUuid
    """)
    abstract fun observe(timeslotUuid: String): Flow<TimeSlotSchema?>

    suspend fun get(timeslotUuid: String): TimeSlotSchema? {
        return observe(timeslotUuid).first()
    }
}