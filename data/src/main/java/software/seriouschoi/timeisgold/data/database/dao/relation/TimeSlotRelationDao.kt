package software.seriouschoi.timeisgold.data.database.dao.relation

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import software.seriouschoi.timeisgold.data.database.relations.TimeSlotRelation

@Dao
internal abstract class TimeSlotRelationDao {
    @Transaction
    @Query("SELECT * FROM TimeSlotSchema WHERE uuid = :timeSlotUuid")
    abstract fun getRelation(timeSlotUuid: String): TimeSlotRelation?
}