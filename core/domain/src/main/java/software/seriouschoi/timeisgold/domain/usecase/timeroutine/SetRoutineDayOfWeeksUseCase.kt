package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 15.
 * jhchoi
 */
class SetRoutineDayOfWeeksUseCase @Inject constructor(
    private val routineRepository: TimeRoutineRepositoryPort,
) {
    suspend fun invoke(dayOfWeeks: List<DayOfWeek>, currentDayOfWeek: DayOfWeek): DomainResult<Unit> {
        return routineRepository.setDayOfWeeks(
            dayOfWeeks,
            currentDayOfWeek
        ).asDomainResult()
    }
}