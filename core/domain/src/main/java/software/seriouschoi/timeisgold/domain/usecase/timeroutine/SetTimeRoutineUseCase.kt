package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import javax.inject.Inject

class SetTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeRoutineDomainService: TimeRoutineDomainService,
) {
    suspend operator fun invoke(
        timeRoutineDefinition: TimeRoutineDefinition
    ): DomainResult<String> {
        val validCheck = timeRoutineDomainService.isValidForAdd(timeRoutineDefinition)
        if (validCheck is DomainResult.Failure) return validCheck

        return timeRoutineRepositoryPort.saveTimeRoutineDefinition(timeRoutineDefinition)
    }
}