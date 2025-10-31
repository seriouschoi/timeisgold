package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema

@Dao
internal abstract class TimeSlotDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insert(timeSlot: TimeSlotSchema): Long

    @Update
    abstract fun update(timeSlots: TimeSlotSchema): Int

    @Query(
        """
        DELETE FROM TimeSlotSchema 
        WHERE uuid = :slotUuid
    """
    )
    abstract fun delete(slotUuid: String)

    @Query(
        """
        SELECT * FROM TimeSlotSchema WHERE uuid = :timeslotUuid
    """
    )
    abstract fun watch(timeslotUuid: String): Flow<TimeSlotSchema?>

    @Query(
        """
        SELECT * FROM TimeSlotSchema WHERE timeRoutineId = :routineId
    """
    )
    abstract fun watchList(routineId: Long): Flow<List<TimeSlotSchema>>

    @Query(
        """
        SELECT * FROM TimeSlotSchema WHERE uuid = :timeslotUuid
    """
    )
    abstract suspend fun get(timeslotUuid: String): TimeSlotSchema?

    @Query(
        """
        SELECT * FROM TimeSlotSchema WHERE id = :id
    """
    )
    abstract suspend fun get(id: Long): TimeSlotSchema?
}