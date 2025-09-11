package software.seriouschoi.timeisgold.domain.services

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
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

        val existingDays = timeRoutineRepository.getAllDayOfWeeks()
        val conflictDays = existingDays.filter {
            newDays.contains(it)
        }
        if (conflictDays.isNotEmpty()) {
            throw TIGException.RoutineConflict(
                conflictDays = conflictDays.toSet()
            )
        }
    }

    suspend fun isValidForAdd(newRoutine: TimeRoutineDefinition): DomainResult<Boolean> {
        if(newRoutine.timeRoutine.title.isEmpty()) {
            return DomainResult.Failure(DomainError.Validation.EmptyTitle)
        }
        val newDays = newRoutine.dayOfWeeks.map { it.dayOfWeek }
        if(newDays.isEmpty()) {
            return DomainResult.Failure(DomainError.Validation.NoSelectedDayOfWeek)
        }

        val existingDays = timeRoutineRepository.getAllTimeRoutineDefinitions().filter {
            it.timeRoutine.uuid != newRoutine.timeRoutine.uuid
        }.map {
            it.dayOfWeeks.map { it.dayOfWeek }
        }.flatten()

        val conflictDays = existingDays.filter {
            newDays.contains(it)
        }
        if (conflictDays.isNotEmpty()) {
            return DomainResult.Failure(DomainError.Conflict.DayOfWeek)
        }

        return DomainResult.Success(true)
    }

}