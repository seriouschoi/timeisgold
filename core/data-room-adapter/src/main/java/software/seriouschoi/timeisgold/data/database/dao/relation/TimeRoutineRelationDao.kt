package software.seriouschoi.timeisgold.data.database.dao.relation

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.relations.TimeRoutineRelation
import software.seriouschoi.timeisgold.data.database.relations.TimeRoutineDayOfWeekRelation
import java.time.DayOfWeek

@Deprecated("해당 개념은 DatabaseView로 대체될 예정.")
@Dao
internal abstract class TimeRoutineRelationDao {
    @Transaction
    @Query("""
        SELECT * 
        FROM TimeRoutineSchema 
        WHERE uuid = :uuid
    """)
    abstract fun get(uuid: String): TimeRoutineRelation?

    @Transaction
    @Query("""
        SELECT * 
        FROM TimeRoutineSchema 
        WHERE uuid = :uuid
    """)
    abstract fun getFlow(uuid: String): Flow<TimeRoutineRelation?>

    @Transaction
    @Query(
        """
        SELECT * 
        FROM TimeRoutineDayOfWeekSchema 
        WHERE dayOfWeek = :week
    """
    )
    abstract fun getByDayOfWeek(week: DayOfWeek): List<TimeRoutineDayOfWeekRelation>
}
