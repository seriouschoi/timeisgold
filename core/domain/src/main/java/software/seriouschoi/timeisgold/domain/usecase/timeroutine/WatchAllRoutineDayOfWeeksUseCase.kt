package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.port.NewRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 11.
 * jhchoi
 */
class WatchAllRoutineDayOfWeeksUseCase @Inject constructor(
    val timeRoutineRepository: NewRoutineRepositoryPort
) {

    fun invoke(): Flow<DomainResult<Set<DayOfWeek>>> {
        return timeRoutineRepository.watchAllDayOfWeeks().map {
            it.asDomainResult()
        }
    }
}