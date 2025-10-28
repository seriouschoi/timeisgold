package software.seriouschoi.timeisgold.domain.port

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import java.time.DayOfWeek

@Deprecated("PlanRepositoryPort")
interface TimeRoutineRepositoryPort {
    suspend fun saveTimeRoutineComposition(composition: TimeRoutineComposition): DataResult<String>

    fun watchCompositionByDayOfWeek(dayOfWeek: DayOfWeek): Flow<TimeRoutineComposition?>
    suspend fun getCompositionByUuid(timeRoutineUuid: String): TimeRoutineComposition?

    fun observeCompositionByUuidFlow(timeRoutineUuid: String): Flow<TimeRoutineComposition?>

    fun observeTimeRoutineByDayOfWeek(day: DayOfWeek): Flow<TimeRoutineEntity?>
    suspend fun deleteTimeRoutine(timeRoutineUuid: String): DataResult<Unit>

    suspend fun saveTimeRoutineDefinition(routine: TimeRoutineDefinition): DataResult<String>
    fun observeTimeRoutineDefinitionByDayOfWeek(dayOfWeek: DayOfWeek): Flow<TimeRoutineDefinition?>
    suspend fun getAllTimeRoutineDefinitions() : List<TimeRoutineDefinition>
    suspend fun getAllDayOfWeeks(): List<DayOfWeek>
    suspend fun setTimeSlotList(
        routineUuid: String,
        incomingSlots: List<TimeSlotEntity>
    ): DataResult<Unit>

    fun watchAllRoutineDayOfWeeks(): Flow<DataResult<List<DayOfWeek>>>
    suspend fun setRoutineTitle(title: String, dayOfWeek: DayOfWeek): DataResult<Unit>
    suspend fun setDayOfWeeks(dayOfWeeks: List<DayOfWeek>, currentDayOfWeek: DayOfWeek): DataResult<Unit>
}
