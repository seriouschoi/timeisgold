package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineData
import software.seriouschoi.timeisgold.domain.policy.TimeRoutinePolicy
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import javax.inject.Inject

class AddTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort,
    private val timeRoutinePolicy: TimeRoutinePolicy
) {
    suspend operator fun invoke(timeRoutine: TimeRoutineData) {
        val allTimeRoutineList = timeRoutineRepositoryPort.getAllTimeRoutines()
        timeRoutinePolicy.checkCanAdd(
            allTimeRoutineList, timeRoutine
        )
        timeRoutineRepositoryPort.addTimeRoutine(timeRoutine)
    }
}