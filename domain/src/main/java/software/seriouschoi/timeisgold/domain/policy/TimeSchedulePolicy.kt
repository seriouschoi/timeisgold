package software.seriouschoi.timeisgold.domain.policy

import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.exception.TIGException

/**
 * Created by jhchoi on 2025. 8. 8.
 * jhchoi@neofect.com
 */
class TimeSchedulePolicy {
    fun checkCanAdd(
        scheduleList: List<TimeScheduleData>,
        timeSchedule: TimeScheduleData
    ) {
        val existingDays =
            scheduleList.map { it.dayOfWeekList.map { it.dayOfWeek } }.flatten().toSet()
        val newDays = timeSchedule.dayOfWeekList.map { it.dayOfWeek }.toSet()
        val conflictDays = existingDays.filter {
            newDays.contains(it)
        }
        if (conflictDays.isNotEmpty()) {
            throw TIGException.ScheduleConflict(
                conflictDays = conflictDays.toSet()
            )
        }
    }
}