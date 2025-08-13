package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.repositories.TimeRoutineRepository

class DeleteTimeRoutineUseCase(
    private val timeRoutineRepository: TimeRoutineRepository
) {
    suspend operator fun invoke(uuid: String) {
        timeRoutineRepository.deleteTimeRoutine(uuid)
    }
}