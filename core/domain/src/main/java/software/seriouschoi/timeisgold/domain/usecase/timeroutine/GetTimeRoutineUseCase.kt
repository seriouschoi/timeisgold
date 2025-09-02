package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.ValidationCode
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

class GetTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(dayOfWeek: DayOfWeek): Flow<DomainResult<TimeRoutineComposition>> {
        return timeRoutineRepositoryPort.observeCompositionByDayOfWeek(dayOfWeek).flatMapLatest {
            if(it == null) {
                flowOf(DomainResult.Failure(DomainError.Validation(ValidationCode.TimeRoutine.DayOfWeekEmpty)))
            } else {
                flowOf(DomainResult.Success(it))
            }
        }
    }
}