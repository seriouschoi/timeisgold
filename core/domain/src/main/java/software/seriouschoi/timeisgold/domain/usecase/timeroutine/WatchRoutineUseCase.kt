package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 28.
 * jhchoi
 */
class WatchRoutineUseCase @Inject constructor(
    private val routineRepository: TimeRoutineRepositoryPort
) {
    suspend fun invoke(dayOfWeek: DayOfWeek): Flow<DomainResult<MetaEnvelope<TimeRoutineVO>>> {
        return routineRepository.watchRoutine(dayOfWeek).map {
            it.asDomainResult()
        }
    }
}