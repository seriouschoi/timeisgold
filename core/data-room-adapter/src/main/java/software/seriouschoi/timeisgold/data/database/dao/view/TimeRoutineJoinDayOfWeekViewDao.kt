package software.seriouschoi.timeisgold.data.database.dao.view

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.view.TimeRoutineJoinDayOfWeekView
import java.time.DayOfWeek

@Dao
internal abstract class TimeRoutineJoinDayOfWeekViewDao {
    @Query(
        """
        SELECT * FROM TimeRoutineJoinDayOfWeekView
        ORDER BY routineCreateTime DESC
        LIMIT 1
    """
    )
    abstract fun getLatestByDayOfWeek(week: DayOfWeek): Flow<TimeRoutineJoinDayOfWeekView?>

    @Query(
        """
        SELECT DISTINCT dayOfWeek FROM TimeRoutineJoinDayOfWeekView
        WHERE routineUuid = :aTimeRoutineUuid
    """
    )
    abstract fun getDayOfWeeksByTimeRoutine(aTimeRoutineUuid: String): Flow<List<DayOfWeek>>

    @Query(
        """
        SELECT DISTINCT dayOfWeek FROM TimeRoutineJoinDayOfWeekView
    """
    )
    abstract fun getAllDayOfWeeks(): Flow<List<DayOfWeek>>
}