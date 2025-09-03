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

    fun upsert(timeSlot: TimeSlotSchema) {
        if(timeSlot.id == null) { insert(timeSlot) }
        else { update(timeSlot) }
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