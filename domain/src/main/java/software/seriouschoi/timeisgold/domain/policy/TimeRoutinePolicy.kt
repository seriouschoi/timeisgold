package software.seriouschoi.timeisgold.domain.policy

import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineData
import software.seriouschoi.timeisgold.domain.exception.TIGException

/**
 * Created by jhchoi on 2025. 8. 8.
 * jhchoi@neofect.com
 */
class TimeRoutinePolicy {
    fun checkCanAdd(
        routineList: List<TimeRoutineData>,
        timeRoutine: TimeRoutineData
    ) {
        val existingDays =
            routineList.map { it.dayOfWeekList.map { it.dayOfWeek } }.flatten().toSet()
        val newDays = timeRoutine.dayOfWeekList.map { it.dayOfWeek }.toSet()
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