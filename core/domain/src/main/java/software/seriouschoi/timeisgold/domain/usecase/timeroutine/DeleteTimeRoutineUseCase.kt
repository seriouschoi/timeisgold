package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import javax.inject.Inject

class DeleteTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
) {
    suspend fun invoke(uuid: String): DomainResult<Boolean> {
        val deleteResult = timeRoutineRepositoryPort.deleteTimeRoutine(uuid)
        when(deleteResult) {
            is DomainResult.Success -> {
                val resultValue = deleteResult.value
                return DomainResult.Success(resultValue == 1)
            }
            is DomainResult.Failure -> {
                return DomainResult.Failure(deleteResult.error)
            }
        }
    }
}