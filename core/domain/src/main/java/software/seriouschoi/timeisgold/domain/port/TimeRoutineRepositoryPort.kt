package software.seriouschoi.timeisgold.domain.port

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.domain.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineEntity
import java.time.DayOfWeek

interface TimeRoutineRepositoryPort {
    suspend fun addTimeRoutineComposition(timeRoutine: TimeRoutineComposition)
    suspend fun setTimeRoutineComposition(composition: TimeRoutineComposition)
    fun getTimeRoutineCompositionByDayOfWeek(dayOfWeek: DayOfWeek): Flow<TimeRoutineComposition?>
    fun getTimeRoutineCompositionByUuid(timeRoutineUuid: String): Flow<TimeRoutineComposition?>

    fun getTimeRoutineByDayOfWeek(day: DayOfWeek): Flow<TimeRoutineEntity?>
    suspend fun deleteTimeRoutine(timeRoutineUuid: String)
    fun getAllDayOfWeeks(): Flow<List<DayOfWeek>>
}
