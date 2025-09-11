package software.seriouschoi.timeisgold.domain.port

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import java.time.DayOfWeek

interface TimeRoutineRepositoryPort {
    @Deprecated("use upsert")
    suspend fun addTimeRoutineComposition(timeRoutine: TimeRoutineComposition)
    @Deprecated("use upsert")
    suspend fun setTimeRoutineComposition(composition: TimeRoutineComposition)

    suspend fun saveTimeRoutineComposition(composition: TimeRoutineComposition): DomainResult<String>

    fun observeCompositionByDayOfWeek(dayOfWeek: DayOfWeek): Flow<TimeRoutineComposition?>
    suspend fun getCompositionByUuid(timeRoutineUuid: String): TimeRoutineComposition?

    fun observeCompositionByUuidFlow(timeRoutineUuid: String): Flow<TimeRoutineComposition?>

    fun observeTimeRoutineByDayOfWeek(day: DayOfWeek): Flow<TimeRoutineEntity?>
    suspend fun deleteTimeRoutine(timeRoutineUuid: String): DomainResult<Int>
    fun observeAllRoutinesDayOfWeeks(): Flow<List<DayOfWeek>>

    suspend fun saveTimeRoutineDefinition(routine: TimeRoutineDefinition): DomainResult<String>
    fun observeTimeRoutineDefinitionByDayOfWeek(dayOfWeek: DayOfWeek): Flow<TimeRoutineDefinition?>
    suspend fun getAllTimeRoutineDefinitions() : List<TimeRoutineDefinition>
    suspend fun getAllDayOfWeeks(): List<DayOfWeek>
}
