package software.seriouschoi.timeisgold.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import software.seriouschoi.timeisgold.data.database.relations.TimeSlotWithExtrasRelation

@Dao
internal abstract class TimeSlotWithExtrasRelationDao {
    @Transaction
    @Query("SELECT * FROM TimeSlotEntity WHERE uuid = :timeSlotUuid")
    abstract fun getRelation(timeSlotUuid: String): TimeSlotWithExtrasRelation?
}