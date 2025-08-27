package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema

@Dao
internal abstract class TimeSlotDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insert(timeSlots: TimeSlotSchema): Long

    @Update
    abstract fun update(timeSlots: TimeSlotSchema)

    @Delete
    abstract fun delete(entity: TimeSlotSchema)

    @Query("SELECT * FROM TimeSlotSchema WHERE uuid = :timeslotUuid")
    abstract fun get(timeslotUuid: String): Flow<TimeSlotSchema?>
}