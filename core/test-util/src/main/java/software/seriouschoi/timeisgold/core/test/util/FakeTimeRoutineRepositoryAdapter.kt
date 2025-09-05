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
) : TimeRoutineRepositoryPort {

    @Deprecated("use upsert")
    override suspend fun addTimeRoutineComposition(timeRoutine: TimeRoutineComposition) {
    }

    @Deprecated("use upsert")
    override suspend fun setTimeRoutineComposition(composition: TimeRoutineComposition) {
    }

    override suspend fun saveTimeRoutineComposition(composition: TimeRoutineComposition): DomainResult<String> {
        // Fake Adapter는 adapter의 동작을 검증하지 않으므로, 구현하지 않는다.
        // 구현할 경우, 불필요현 유지보수와 결합이 생기게 됨.

        return DomainResult.Success(composition.timeRoutine.uuid)
    }

    override fun observeCompositionByDayOfWeek(dayOfWeek: DayOfWeek): Flow<TimeRoutineComposition?> {
        return flow {
            val forEmit = mockTimeRoutines.find {
                it.dayOfWeeks.any {
                    it.dayOfWeek == dayOfWeek
                }
            }
            emit(forEmit)
        }
    }

    override suspend fun getCompositionByUuid(timeRoutineUuid: String): TimeRoutineComposition? {
        return mockTimeRoutines.find { it.timeRoutine.uuid == timeRoutineUuid }
    }

    override fun observeCompositionByUuidFlow(timeRoutineUuid: String): Flow<TimeRoutineComposition?> {
        return flow {
            val forEmit = getCompositionByUuid(timeRoutineUuid)
            emit(forEmit)
        }
    }

    override fun observeTimeRoutineByDayOfWeek(day: DayOfWeek): Flow<TimeRoutineEntity?> {
        return flow {
            val forEmit = mockTimeRoutines.find {
                it.dayOfWeeks.any {
                    it.dayOfWeek == day
                }
            }?.timeRoutine
            emit(forEmit)
        }
    }

    override suspend fun deleteTimeRoutine(timeRoutineUuid: String) {
    }

    override fun observeAllRoutinesDayOfWeeks(): Flow<List<DayOfWeek>> {
        return flow {
            val forEmit = mockTimeRoutines.map { it.dayOfWeeks.map { it.dayOfWeek } }.flatten()
            emit(forEmit)
        }
    }
}