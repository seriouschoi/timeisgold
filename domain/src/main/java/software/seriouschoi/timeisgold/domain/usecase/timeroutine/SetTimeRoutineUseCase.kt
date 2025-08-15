package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineData
import software.seriouschoi.timeisgold.domain.policy.TimeRoutinePolicy
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import javax.inject.Inject

class SetTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeRoutinePolicy: TimeRoutinePolicy
) {
    suspend operator fun invoke(timeRoutine: TimeRoutineData) {
        val routineListForPolicy = timeRoutineRepositoryPort.getAllTimeRoutines()
        timeRoutinePolicy.checkCanAdd(
            routineListForPolicy, timeRoutine
        )
        timeRoutineRepositoryPort.setTimeRoutine(timeRoutine)
    }
}