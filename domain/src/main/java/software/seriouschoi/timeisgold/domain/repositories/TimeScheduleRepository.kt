package software.seriouschoi.timeisgold.domain.repositories

import software.seriouschoi.timeisgold.domain.data.timeschedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.data.timeschedule.TimeScheduleDetailData
import java.time.DayOfWeek

interface TimeScheduleRepository {
    suspend fun addTimeSchedule(timeSchedule: TimeScheduleData)
    suspend fun getTimeScheduleDetail(week: DayOfWeek): TimeScheduleDetailData?
    suspend fun getTimeScheduleDetailByUuid(uuid: String): TimeScheduleDetailData?
    suspend fun getAllTimeSchedules(): List<TimeScheduleData>
    suspend fun setTimeSchedule(timeSchedule: TimeScheduleData)
    suspend fun deleteTimeSchedule(timeScheduleUuid: String)
}
