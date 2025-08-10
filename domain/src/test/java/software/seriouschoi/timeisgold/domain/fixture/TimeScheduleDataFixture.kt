package software.seriouschoi.timeisgold.domain.fixture

import software.seriouschoi.timeisgold.domain.data.timeschedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.data.timeschedule.TimeScheduleDayOfWeekData
import software.seriouschoi.timeisgold.domain.data.timeschedule.TimeScheduleDetailData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotData
import java.time.DayOfWeek
import java.time.LocalTime
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

    fun createTimeScheduleDetail(
        dayOfWeekList: List<DayOfWeek>,
    ): TimeScheduleDetailData {
        return TimeScheduleDetailData(
            timeScheduleData = createTimeSchedule(dayOfWeekList),
            timeSlotList = createTimeSlotList()
        )
    }

    fun createTimeSlotList(): List<TimeSlotData> {
        val baseTime = LocalTime.of(10, 0, 0)

        return (0..10).map { i ->
            val startTime = baseTime.plusHours(i.toLong())
            val endTime = startTime.plusHours(1)

            TimeSlotData(
                uuid = UUID.randomUUID().toString(),
                title = "test_$i",
                startTime = startTime,
                endTime = endTime,
                createTime = System.currentTimeMillis()
            )
        }
    }
}