package software.seriouschoi.timeisgold.domain.repositories

import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleDetailData
import java.time.DayOfWeek

interface TimeScheduleRepository {
    suspend fun addTimeSchedule(timeSchedule: TimeScheduleData)
    suspend fun getTimeSchedule(week: DayOfWeek): TimeScheduleDetailData?
    suspend fun setTimeSchedule(timeSchedule: TimeScheduleData)
    suspend fun deleteTimeSchedule(timeScheduleUuid: String)
    suspend fun getTimeScheduleByUuid(uuid: String): TimeScheduleDetailData?
}
