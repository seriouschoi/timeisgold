package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.domain.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

class GetTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort
) {
    operator fun invoke(week: DayOfWeek) : Flow<TimeRoutineComposition?> {
        return timeRoutineRepositoryPort.getTimeRoutineCompositionByDayOfWeek(week)
    }
}