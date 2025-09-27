package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

class WatchTimeRoutineCompositionUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(dayOfWeek: DayOfWeek): Flow<DomainResult<TimeRoutineComposition>> {
        return timeRoutineRepositoryPort.watchCompositionByDayOfWeek(dayOfWeek).flatMapLatest {
            if(it == null) {
                flowOf(DomainResult.Failure(DomainError.NotFound.TimeRoutine))
            } else {
                flowOf(DomainResult.Success(it))
            }
        }
    }
}