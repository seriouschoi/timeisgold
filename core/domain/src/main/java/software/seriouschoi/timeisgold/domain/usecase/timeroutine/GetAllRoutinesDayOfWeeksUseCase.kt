package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

class GetAllRoutinesDayOfWeeksUseCase @Inject constructor(
    private val timeRoutineRepository: TimeRoutineRepositoryPort,
) {

    suspend fun invoke() : List<DayOfWeek> {
        return timeRoutineRepository.getAllDayOfWeeks()
    }
}