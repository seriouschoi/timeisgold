package software.seriouschoi.timeisgold.domain.services

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.domain.data.ConflictCode
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.ValidationCode
import software.seriouschoi.timeisgold.domain.exception.TIGException
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import javax.inject.Inject

class TimeRoutineDomainService @Inject constructor(
    private val timeRoutineRepository: TimeRoutineRepositoryPort,
) {
    @Deprecated("Use isValidForAdd")
    suspend fun checkCanAdd(newRoutine: TimeRoutineComposition) {
        if(newRoutine.timeRoutine.title.isEmpty()) {
            throw TIGException.EmptyTitle()
        }
        val newDays = newRoutine.dayOfWeeks.map { it.dayOfWeek }
        if(newDays.isEmpty()) {
            throw TIGException.EmptyDayOfWeeks()
        }

        val existingDays = timeRoutineRepository.observeAllRoutinesDayOfWeeks().first()
        val conflictDays = existingDays.filter {
            newDays.contains(it)
        }
        if (conflictDays.isNotEmpty()) {
            throw TIGException.RoutineConflict(
                conflictDays = conflictDays.toSet()
            )
        }
    }

    suspend fun isValidForAdd(newRoutine: TimeRoutineComposition): DomainResult<Boolean> {
        if(newRoutine.timeRoutine.title.isEmpty()) {
            return DomainResult.Failure(DomainError.Validation(ValidationCode.TimeRoutine.Title))
        }
        val newDays = newRoutine.dayOfWeeks.map { it.dayOfWeek }
        if(newDays.isEmpty()) {
            return DomainResult.Failure(DomainError.Validation(ValidationCode.TimeRoutine.DayOfWeekEmpty))
        }

        val existingDays = timeRoutineRepository.observeAllRoutinesDayOfWeeks().first()
        val conflictDays = existingDays.filter {
            newDays.contains(it)
        }
        if (conflictDays.isNotEmpty()) {
            return DomainResult.Failure(DomainError.Conflict(ConflictCode.TimeRoutine.DayOfWeek))
        }

        return DomainResult.Success(true)
    }

}