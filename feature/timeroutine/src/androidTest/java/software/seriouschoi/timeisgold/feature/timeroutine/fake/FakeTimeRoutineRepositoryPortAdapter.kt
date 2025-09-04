package software.seriouschoi.timeisgold.feature.timeroutine.fake

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 9. 4.
 * jhchoi
 */
object FakeTimeRoutineRepositoryPortAdapter : TimeRoutineRepositoryPort {
    override suspend fun addTimeRoutineComposition(timeRoutine: TimeRoutineComposition) {
        TODO("Not yet implemented")
    }

    override suspend fun setTimeRoutineComposition(composition: TimeRoutineComposition) {
        TODO("Not yet implemented")
    }

    override suspend fun saveTimeRoutineComposition(composition: TimeRoutineComposition): DomainResult<String> {
        TODO("Not yet implemented")
    }

    override fun observeCompositionByDayOfWeek(dayOfWeek: DayOfWeek): Flow<TimeRoutineComposition?> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompositionByUuid(timeRoutineUuid: String): TimeRoutineComposition? {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }
}