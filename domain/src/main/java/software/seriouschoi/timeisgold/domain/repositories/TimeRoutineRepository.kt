package software.seriouschoi.timeisgold.domain.repositories

import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineData
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDetailData
import java.time.DayOfWeek

interface TimeRoutineRepository {
    suspend fun addTimeRoutine(timeRoutine: TimeRoutineData)
    suspend fun getTimeRoutineDetail(week: DayOfWeek): TimeRoutineDetailData?
    suspend fun getTimeRoutineDetailByUuid(uuid: String): TimeRoutineDetailData?
    suspend fun getAllTimeRoutines(): List<TimeRoutineData>
    suspend fun setTimeRoutine(timeRoutine: TimeRoutineData)
    suspend fun deleteTimeRoutine(timeRoutineUuid: String)
}
