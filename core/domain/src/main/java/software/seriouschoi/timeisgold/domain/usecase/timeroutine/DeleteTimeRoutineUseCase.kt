package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import javax.inject.Inject

class DeleteTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
) {
    suspend fun invoke(uuid: String): DomainResult<Unit> {
        val deleteResult = timeRoutineRepositoryPort.deleteTimeRoutine(uuid)
        return deleteResult.asDomainResult()
    }
}