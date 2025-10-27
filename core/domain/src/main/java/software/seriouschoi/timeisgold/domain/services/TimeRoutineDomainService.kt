package software.seriouschoi.timeisgold.domain.services

import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import javax.inject.Inject

class TimeRoutineDomainService @Inject constructor(
    private val timeRoutineRepository: TimeRoutineRepositoryPort,
) {
    suspend fun isValidForAdd(newRoutine: TimeRoutineDefinition): DomainResult<Unit> {
        val newDays = newRoutine.dayOfWeeks.map { it.dayOfWeek }
        if (newDays.isEmpty()) {
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

        return DomainResult.Success(Unit)
    }

}