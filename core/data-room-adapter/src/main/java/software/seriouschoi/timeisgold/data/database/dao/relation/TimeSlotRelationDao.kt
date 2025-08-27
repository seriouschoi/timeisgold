package software.seriouschoi.timeisgold.data.database.dao.relation

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import software.seriouschoi.timeisgold.data.database.relations.TimeSlotRelation

@Deprecated("해당 개념은 DatabaseView로 대체될 예정.")
@Dao
internal abstract class TimeSlotRelationDao {
    @Transaction
    @Query("SELECT * FROM TimeSlotSchema WHERE uuid = :timeSlotUuid")
    abstract fun getRelation(timeSlotUuid: String): TimeSlotRelation?
}