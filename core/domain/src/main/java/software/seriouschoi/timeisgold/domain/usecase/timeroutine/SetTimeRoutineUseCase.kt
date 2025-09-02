package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import java.time.DayOfWeek
import javax.inject.Inject

class SetTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeRoutineDomainService: TimeRoutineDomainService,
) {
    // TODO: make test.
    suspend operator fun invoke(
        routine: TimeRoutineEntity,
        dayOfWeeks: List<DayOfWeek>
    ): DomainResult<String> {
        val routineFromDB = timeRoutineRepositoryPort.getCompositionByUuid(routine.uuid)

        val newRoutineCompo = TimeRoutineComposition(
            timeRoutine = routine,
            dayOfWeeks = dayOfWeeks.map {
                TimeRoutineDayOfWeekEntity(
                    dayOfWeek = it
                )
            }.toSet(),
            timeSlots = routineFromDB?.timeSlots ?: emptyList()
        )

        val validCheck = timeRoutineDomainService.isValidForAdd(newRoutineCompo)
        if (validCheck is DomainResult.Failure) return validCheck

        return timeRoutineRepositoryPort.saveTimeRoutineComposition(newRoutineCompo)
    }
}