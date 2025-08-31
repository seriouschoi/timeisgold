package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.policy.TimeRoutinePolicy
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

class SetTimeRoutineCompositionUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeRoutinePolicy: TimeRoutinePolicy,
) {
    suspend operator fun invoke(timeRoutineComposition: TimeRoutineComposition) {
        val existingDays: List<DayOfWeek> = timeRoutineRepositoryPort.getAllDayOfWeeks().first()
        timeRoutinePolicy.checkCanAdd(
            existingDays, timeRoutineComposition.dayOfWeeks.map { it.dayOfWeek }
        )
        val routineFromDB =
            timeRoutineRepositoryPort.getTimeRoutineCompositionByUuid(timeRoutineComposition.timeRoutine.uuid)
                .first()
        if (routineFromDB != null) {
            timeRoutineRepositoryPort.setTimeRoutineComposition(timeRoutineComposition)
        } else {
            timeRoutineRepositoryPort.addTimeRoutineComposition(timeRoutineComposition)
        }
    }
}