package software.seriouschoi.timeisgold.core.test.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 9. 4.
 * jhchoi
 */
class FakeTimeRoutineRepositoryAdapter(
    private val mockTimeRoutines: List<TimeRoutineComposition>
): TimeRoutineRepositoryPort {
    @Deprecated("use upsert")
    override suspend fun addTimeRoutineComposition(timeRoutine: TimeRoutineComposition) {
        TODO("Not yet implemented")
    }

    @Deprecated("use upsert")
    override suspend fun setTimeRoutineComposition(composition: TimeRoutineComposition) {
        TODO("Not yet implemented")
    }

    override suspend fun saveTimeRoutineComposition(composition: TimeRoutineComposition): DomainResult<String> {
        return DomainResult.Success(composition.timeRoutine.uuid)
    }

    override fun observeCompositionByDayOfWeek(dayOfWeek: DayOfWeek): Flow<TimeRoutineComposition?> {
        return flow {
            val compo = mockTimeRoutines.find {
                it.dayOfWeeks.any {
                    it.dayOfWeek == dayOfWeek
                }
            }
            emit(compo)
        }
    }

    override suspend fun getCompositionByUuid(timeRoutineUuid: String): TimeRoutineComposition? {
        return mockTimeRoutines.find { it.timeRoutine.uuid == timeRoutineUuid }
    }

    override fun observeCompositionByUuidFlow(timeRoutineUuid: String): Flow<TimeRoutineComposition?> {
        TODO("Not yet implemented")
    }

    override fun observeTimeRoutineByDayOfWeek(day: DayOfWeek): Flow<TimeRoutineEntity?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTimeRoutine(timeRoutineUuid: String) {
        TODO("Not yet implemented")
    }

    override fun observeAllRoutinesDayOfWeeks(): Flow<List<DayOfWeek>> {
        return flow {
            val dayOfWeeks = mockTimeRoutines.map { it.dayOfWeeks.map { it.dayOfWeek } }.flatten()
            emit(dayOfWeeks)
        }
    }
}