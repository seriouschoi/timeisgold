package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import javax.inject.Inject

class SetTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeRoutineDomainService: TimeRoutineDomainService,
) {
    suspend fun invoke(
        timeRoutineDefinition: TimeRoutineDefinition
    ): DomainResult<String> {
        val validResult = timeRoutineDomainService.isValidForAdd(timeRoutineDefinition)
        if(validResult is DomainResult.Failure) return validResult

        return timeRoutineRepositoryPort.saveTimeRoutineDefinition(timeRoutineDefinition).asDomainResult()
    }
}