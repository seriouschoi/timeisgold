package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineData
import software.seriouschoi.timeisgold.domain.policy.TimeRoutinePolicy
import software.seriouschoi.timeisgold.domain.repositories.TimeRoutineRepository
import javax.inject.Inject

class SetTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepository: TimeRoutineRepository,
    private val timeRoutinePolicy: TimeRoutinePolicy
) {
    suspend operator fun invoke(timeRoutine: TimeRoutineData) {
        val routineListForPolicy = timeRoutineRepository.getAllTimeRoutines()
        timeRoutinePolicy.checkCanAdd(
            routineListForPolicy, timeRoutine
        )
        timeRoutineRepository.setTimeRoutine(timeRoutine)
    }
}