package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.repositories.TimeRoutineRepository
import javax.inject.Inject

class DeleteTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepository: TimeRoutineRepository
) {
    suspend operator fun invoke(uuid: String) {
        timeRoutineRepository.deleteTimeRoutine(uuid)
    }
}