package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import javax.inject.Inject

@Deprecated("use set time routine use case")
class AddTimeRoutineCompositionUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeRoutineService: TimeRoutineDomainService
) {
    suspend operator fun invoke(timeRoutine: TimeRoutineComposition) {
        timeRoutineService.checkCanAdd(
            timeRoutine
        )
        timeRoutineRepositoryPort.addTimeRoutineComposition(timeRoutine)
    }
}