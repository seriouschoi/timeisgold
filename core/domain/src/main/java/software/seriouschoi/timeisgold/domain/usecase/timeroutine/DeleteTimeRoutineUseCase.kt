package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import javax.inject.Inject

class DeleteTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort
) {
    suspend operator fun invoke(uuid: String) {
        timeRoutineRepositoryPort.deleteTimeRoutine(uuid)
    }
}