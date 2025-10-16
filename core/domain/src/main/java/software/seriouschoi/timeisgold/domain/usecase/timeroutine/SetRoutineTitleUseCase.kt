package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 13.
 * jhchoi
 */
class SetRoutineTitleUseCase @Inject constructor(
    private val repository: TimeRoutineRepositoryPort,
) {

    suspend fun invoke(title: String, dayOfWeek: DayOfWeek): DomainResult<Unit> {
        return repository.setRoutineTitle(title, dayOfWeek).asDomainResult()
    }
}