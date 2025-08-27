package software.seriouschoi.timeisgold.data.database.dao.view

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.schema.view.TimeRoutineJoinDayOfWeekView
import java.time.DayOfWeek

@Dao
internal abstract class TimeRoutineJoinDayOfWeekViewDao {
    @Query("""
        SELECT * FROM TimeRoutineJoinDayOfWeekView
        ORDER BY routineCreateTime DESC
        LIMIT 1
    """)
    abstract fun getLatestByDayOfWeek(week: DayOfWeek): Flow<TimeRoutineJoinDayOfWeekView?>

    @Query(
        """
        SELECT dayOfWeek FROM TimeRoutineJoinDayOfWeekView
        WHERE routineUuid = :aTimeRoutineUuid
    """
    )
    abstract fun getDayOfWeeksByTimeRoutine(aTimeRoutineUuid: String): Flow<List<DayOfWeek>>
}