package software.seriouschoi.timeisgold.domain.services

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.exception.TIGException
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import javax.inject.Inject

class TimeRoutineDomainService @Inject constructor(
    private val timeRoutineRepository: TimeRoutineRepositoryPort,
) {
    suspend fun checkCanAdd(newRoutine: TimeRoutineComposition) {
        if(newRoutine.timeRoutine.title.isEmpty()) {
            throw TIGException.EmptyTitle()
        }
        val newDays = newRoutine.dayOfWeeks.map { it.dayOfWeek }
        if(newDays.isEmpty()) {
            throw TIGException.EmptyDayOfWeeks()
        }

        val existingDays = timeRoutineRepository.getAllDayOfWeeks().first()
        val conflictDays = existingDays.filter {
            newDays.contains(it)
        }
        if (conflictDays.isNotEmpty()) {
            throw TIGException.RoutineConflict(
                conflictDays = conflictDays.toSet()
            )
        }
    }

}