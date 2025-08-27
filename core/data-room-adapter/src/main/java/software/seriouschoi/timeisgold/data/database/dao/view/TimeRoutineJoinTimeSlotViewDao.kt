package software.seriouschoi.timeisgold.data.database.dao.view

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.data.database.schema.view.TimeRoutineJoinTimeSlotView

@Dao
internal abstract class TimeRoutineJoinTimeSlotViewDao {
    @Query("""
        SELECT * FROM TimeRoutineJoinTimeSlotView
        WHERE routineUuid = :timeRoutineUuid
        ORDER BY timeSlotStartTime
    """)
    abstract fun getTimeSlotsByTimeRoutine(timeRoutineUuid: String): Flow<List<TimeRoutineJoinTimeSlotView>>
}