package software.seriouschoi.timeisgold.data.database.dao.relation

import androidx.room.Dao
import androidx.room.Query
import software.seriouschoi.timeisgold.data.database.relations.TimeScheduleWithDayOfWeeks

@Dao
internal abstract class TimeScheduleWithDayOfWeeksDao {
    @Query("SELECT * FROM TimeScheduleSchema")
    abstract fun getAll(): List<TimeScheduleWithDayOfWeeks>
}