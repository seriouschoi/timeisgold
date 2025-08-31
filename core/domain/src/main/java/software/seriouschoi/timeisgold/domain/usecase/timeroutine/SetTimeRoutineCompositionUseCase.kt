package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import javax.inject.Inject

class SetTimeRoutineCompositionUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeRoutineDomainService: TimeRoutineDomainService,
) {
    suspend operator fun invoke(timeRoutineComposition: TimeRoutineComposition) {
        timeRoutineDomainService.checkCanAdd(timeRoutineComposition)

        val routineFromDB = timeRoutineRepositoryPort
            .getTimeRoutineCompositionByUuid(timeRoutineComposition.timeRoutine.uuid)
            .first()

        if (routineFromDB != null) {
            timeRoutineRepositoryPort.setTimeRoutineComposition(timeRoutineComposition)
        } else {
            timeRoutineRepositoryPort.addTimeRoutineComposition(timeRoutineComposition)
        }
    }
}