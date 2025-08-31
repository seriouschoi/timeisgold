package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.domain.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.policy.TimeRoutinePolicy
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

class SetTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeRoutinePolicy: TimeRoutinePolicy,
) {
    // TODO: make test.
    suspend operator fun invoke(routine: TimeRoutineEntity, dayOfWeeks: List<DayOfWeek>) {
        val existingDays: List<DayOfWeek> = timeRoutineRepositoryPort.getAllDayOfWeeks().first()
        // TODO: 이거 그냥 루틴 컴포지션을 보내고, 정책에서 리포지토리 의존성을 가지면 안되나..?
        timeRoutinePolicy.checkCanAdd(
            existingDays, dayOfWeeks
        )

        val routineFromDB =
            timeRoutineRepositoryPort.getTimeRoutineCompositionByUuid(routine.uuid)
                .first()

        if (routineFromDB != null) {
            timeRoutineRepositoryPort.setTimeRoutineComposition(
                TimeRoutineComposition(
                    timeRoutine = routine,
                    dayOfWeeks = dayOfWeeks.map {
                        TimeRoutineDayOfWeekEntity(
                            dayOfWeek = it
                        )
                    }.toSet(),
                    timeSlots = routineFromDB.timeSlots
                )
            )
        } else {
            timeRoutineRepositoryPort.addTimeRoutineComposition(
                TimeRoutineComposition(
                    timeRoutine = routine,
                    dayOfWeeks = dayOfWeeks.map {
                        TimeRoutineDayOfWeekEntity(
                            dayOfWeek = it
                        )
                    }.toSet(),
                    timeSlots = emptyList()
                )
            )
        }
    }
}