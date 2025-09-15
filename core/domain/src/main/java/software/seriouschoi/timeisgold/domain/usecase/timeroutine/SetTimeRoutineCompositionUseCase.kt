package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import javax.inject.Inject

class SetTimeRoutineCompositionUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeRoutineDomainService: TimeRoutineDomainService,
) {
    suspend operator fun invoke(timeRoutineComposition: TimeRoutineComposition): DomainResult<String> {
        val routineDefinition = TimeRoutineDefinition(
            timeRoutine = timeRoutineComposition.timeRoutine,
            dayOfWeeks = timeRoutineComposition.dayOfWeeks
        )
        val validResult = timeRoutineDomainService.isValidForAdd(routineDefinition)
        if (validResult is DomainResult.Failure) return validResult

        // TODO: jhchoi 2025. 9. 15. time slot valid check도 해야함.
        
        return timeRoutineRepositoryPort.saveTimeRoutineComposition(timeRoutineComposition)
    }
}