package software.seriouschoi.timeisgold.domain.port

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineData
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDetailData
import java.time.DayOfWeek

interface TimeRoutineRepositoryPort {
    suspend fun addTimeRoutine(timeRoutine: TimeRoutineData)
    fun getTimeRoutineDetail(week: DayOfWeek): Flow<TimeRoutineDetailData?>
    suspend fun getTimeRoutineDetailByUuid(timeRoutineUuid: String): TimeRoutineDetailData?
    suspend fun getAllTimeRoutines(): List<TimeRoutineData>
    suspend fun setTimeRoutine(timeRoutine: TimeRoutineData)
    suspend fun deleteTimeRoutine(timeRoutineUuid: String)
}
