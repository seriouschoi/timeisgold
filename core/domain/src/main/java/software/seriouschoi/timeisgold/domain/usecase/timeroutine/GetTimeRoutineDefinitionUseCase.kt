package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

class GetTimeRoutineDefinitionUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun invoke(dayOfWeek: DayOfWeek): Flow<DomainResult<TimeRoutineDefinition>> {
        return timeRoutineRepositoryPort.observeTimeRoutineDefinitionByDayOfWeek(dayOfWeek).flatMapLatest {
            if(it == null) {
                flowOf(DomainResult.Failure(DomainError.NotFound.TimeRoutine))
            } else {
                flowOf(DomainResult.Success(it))
            }
        }
    }
}