package software.seriouschoi.timeisgold.domain.fixture

import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleDayOfWeekData
import java.time.DayOfWeek
import java.util.UUID

/**
 * Created by jhchoi on 2025. 8. 8.
 * jhchoi@neofect.com
 */
internal object TimeScheduleDataFixture {
    fun createTimeSchedule(dayOfWeekList: List<DayOfWeek>): TimeScheduleData {
        val uuid = UUID.randomUUID().toString()
        return TimeScheduleData(
            timeScheduleName = "test_$uuid",
            uuid = uuid,
            createTime = System.currentTimeMillis(),
            dayOfWeekList = dayOfWeekList.map {
                TimeScheduleDayOfWeekData(
                    dayOfWeek = it,
                    uuid = UUID.randomUUID().toString()
                )
            }
        )
    }
}