package software.seriouschoi.timeisgold.data.database.dao.relation

import androidx.room.Dao
import androidx.room.Query
import software.seriouschoi.timeisgold.data.database.relations.TimeRoutineWithDayOfWeeks

@Dao
internal abstract class TimeRoutineWithDayOfWeeksDao {
    @Query("SELECT * FROM TimeRoutineSchema")
    abstract fun getAll(): List<TimeRoutineWithDayOfWeeks>
}