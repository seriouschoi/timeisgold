package software.seriouschoi.timeisgold.data.database.dao.view

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.data.database.view.TimeRoutineJoinTimeSlotView

@Dao
internal abstract class TimeRoutineJoinTimeSlotViewDao {
    @Query(
        """
        SELECT * FROM TimeRoutineJoinTimeSlotView
        WHERE routineUuid = :timeRoutineUuid
        ORDER BY timeSlotStartTime
    """
    )
    abstract fun observeTimeSlotsByTimeRoutine(timeRoutineUuid: String): Flow<List<TimeRoutineJoinTimeSlotView>>

    suspend fun getTimeSlotsByTimeRoutine(timeRoutineUuid: String): List<TimeRoutineJoinTimeSlotView> {
        return observeTimeSlotsByTimeRoutine(timeRoutineUuid).first()
    }
}