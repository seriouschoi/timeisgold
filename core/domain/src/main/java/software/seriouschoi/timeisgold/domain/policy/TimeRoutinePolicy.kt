package software.seriouschoi.timeisgold.domain.policy

import software.seriouschoi.timeisgold.domain.exception.TIGException
import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 8. 8.
 * jhchoi
 */
class TimeRoutinePolicy {
    fun checkCanAdd(
        existingDays: List<DayOfWeek>,
        newRoutineDays: List<DayOfWeek>
    ) {
        val newDays = newRoutineDays.toSet()
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