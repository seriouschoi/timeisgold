package software.seriouschoi.timeisgold.data.database.dao.relation

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import software.seriouschoi.timeisgold.data.database.relations.TimeRoutineWithDayOfWeeksRelation

@Deprecated("해당 개념은 DatabaseView로 대체될 예정.")
@Dao
internal abstract class TimeRoutineWithDayOfWeeksDao {
    @Transaction
    @Query("SELECT * FROM TimeRoutineSchema")
    abstract fun getAll(): List<TimeRoutineWithDayOfWeeksRelation>
}