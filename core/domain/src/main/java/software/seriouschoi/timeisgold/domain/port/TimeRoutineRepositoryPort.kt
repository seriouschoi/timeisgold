package software.seriouschoi.timeisgold.domain.port

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.domain.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineData
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDetailData
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineEntity
import java.time.DayOfWeek

interface TimeRoutineRepositoryPort {
    suspend fun addTimeRoutine(timeRoutine: TimeRoutineComposition)
    fun getTimeRoutine(week: DayOfWeek): Flow<TimeRoutineEntity?>
    suspend fun getTimeRoutineDetailByUuid(timeRoutineUuid: String): TimeRoutineComposition?
    suspend fun setTimeRoutine(composition: TimeRoutineComposition)
    suspend fun deleteTimeRoutine(timeRoutineUuid: String)
}
