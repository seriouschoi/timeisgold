package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotEntity

@Dao
internal abstract class TimeSlotDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insert(timeSlot: TimeSlotEntity): Long

    @Update
    abstract fun update(timeSlots: TimeSlotEntity): Int

    @Query(
        """
        DELETE FROM TimeSlotEntity 
        WHERE uuid = :slotUuid
    """
    )
    abstract fun delete(slotUuid: String)

    @Query(
        """
        SELECT * FROM TimeSlotEntity WHERE uuid = :timeslotUuid
    """
    )
    abstract fun watch(timeslotUuid: String): Flow<TimeSlotEntity?>

    @Query(
        """
        SELECT * FROM TimeSlotEntity WHERE timeRoutineId = :routineId
    """
    )
    abstract fun watchList(routineId: Long): Flow<List<TimeSlotEntity>>

    @Query(
        """
        SELECT * FROM TimeSlotEntity WHERE uuid = :timeslotUuid
    """
    )
    abstract suspend fun get(timeslotUuid: String): TimeSlotEntity?

    @Query(
        """
        SELECT * FROM TimeSlotEntity WHERE id = :id
    """
    )
    abstract suspend fun get(id: Long): TimeSlotEntity?
}