package software.seriouschoi.timeisgold.data.database.dao.relation

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import software.seriouschoi.timeisgold.data.database.relations.TimeScheduleDayOfWeekRelation
import software.seriouschoi.timeisgold.data.database.relations.TimeScheduleRelation
import java.time.DayOfWeek

@Dao
internal abstract class TimeScheduleRelationDao {
    @Transaction
    @Query("SELECT * FROM TimeScheduleEntity WHERE uuid = :uuid")
    abstract fun get(uuid: String): TimeScheduleRelation?

    @Transaction
    @Query("""
        SELECT * 
        FROM TimeScheduleDayOfWeekEntity 
        WHERE dayOfWeek = :week
    """)
    abstract fun getByDayOfWeek(week: DayOfWeek): List<TimeScheduleDayOfWeekRelation>

}
