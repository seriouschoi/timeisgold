package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import javax.inject.Inject

class GetValidTimeRoutineUseCase @Inject constructor(
    private val service: TimeRoutineDomainService,
) {
    suspend operator fun invoke(
        timeRoutineDefinition: TimeRoutineDefinition
    ): DomainResult<Boolean> {
        return service.isValidForAdd(
            timeRoutineDefinition
        )
    }
}