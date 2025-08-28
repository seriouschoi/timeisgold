package software.seriouschoi.timeisgold.domain.policy

import software.seriouschoi.timeisgold.domain.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.exception.TIGException
import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 8. 8.
 * jhchoi
 */
class TimeRoutinePolicy {
    fun checkCanAdd(
        existingDays: List<DayOfWeek>,
        timeRoutine: TimeRoutineComposition
    ) {
        val newDays = timeRoutine.dayOfWeeks.map { it.dayOfWeek }.toSet()
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