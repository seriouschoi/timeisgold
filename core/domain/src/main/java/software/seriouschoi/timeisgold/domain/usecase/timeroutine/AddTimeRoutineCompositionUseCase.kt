package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.domain.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.policy.TimeRoutinePolicy
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

@Deprecated("use set time routine use case")
class AddTimeRoutineCompositionUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeRoutinePolicy: TimeRoutinePolicy
) {
    suspend operator fun invoke(timeRoutine: TimeRoutineComposition) {
        val existingDays: List<DayOfWeek> = timeRoutineRepositoryPort.getAllDayOfWeeks().first()
        timeRoutinePolicy.checkCanAdd(
            existingDays, timeRoutine
        )
        timeRoutineRepositoryPort.addTimeRoutineComposition(timeRoutine)
    }
}