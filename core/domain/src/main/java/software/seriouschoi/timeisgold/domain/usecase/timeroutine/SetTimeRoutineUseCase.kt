package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.flow.first
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
    suspend operator fun invoke(routine: TimeRoutineEntity, dayOfWeeks: List<DayOfWeek>) {
        val routineFromDB =
            timeRoutineRepositoryPort.getTimeRoutineCompositionByUuid(routine.uuid)
                .first()

        val newRoutineCompo = TimeRoutineComposition(
            timeRoutine = routine,
            dayOfWeeks = dayOfWeeks.map {
                TimeRoutineDayOfWeekEntity(
                    dayOfWeek = it
                )
            }.toSet(),
            timeSlots = routineFromDB?.timeSlots ?: emptyList()
        )

        timeRoutineDomainService.checkCanAdd(newRoutineCompo)

        if (routineFromDB != null) {
            timeRoutineRepositoryPort.setTimeRoutineComposition(newRoutineCompo)
        } else {
            timeRoutineRepositoryPort.addTimeRoutineComposition(newRoutineCompo)
        }
    }
}