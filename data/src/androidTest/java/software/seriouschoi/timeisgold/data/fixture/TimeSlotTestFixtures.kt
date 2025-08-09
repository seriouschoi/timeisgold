package software.seriouschoi.timeisgold.data.fixture

import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleDayOfWeekData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotMemoData
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID

internal object TimeSlotTestFixtures {
    fun createDetailDataList(): List<TimeSlotDetailData> {
        return (0..10).map { i ->
            return@map createDetailTimeSlot()
        }
    }

    fun createDetailTimeSlot(): TimeSlotDetailData {
        val uuid = UUID.randomUUID()
        val memoUuid = UUID.randomUUID()
        val now = LocalTime.now()
        val timeSlotData = TimeSlotData(
            uuid = uuid.toString(),
            title = "test_$uuid",
            startTime = now.minusMinutes(10),
            endTime = now,
            createTime = System.currentTimeMillis()
        )
        val timeSlotMemoData = TimeSlotMemoData(
            uuid = memoUuid.toString(),
            memo = "test_$memoUuid",
            createTime = System.currentTimeMillis()
        )

        return TimeSlotDetailData(
            timeSlotData = timeSlotData,
            timeSlotMemoData = timeSlotMemoData
        )
    }

    fun createTimeSchedule(dayOfWeekList: List<DayOfWeek>): TimeScheduleData {
        val uuid = UUID.randomUUID()
        return TimeScheduleData(
            uuid = uuid.toString(),
            createTime = System.currentTimeMillis(),
            timeScheduleName = "test_schedule_$uuid",
            dayOfWeekList = dayOfWeekList.map {
                createTimeScheduleDayOfWeek(it)
            }
        )
    }

    private fun createTimeScheduleDayOfWeek(dayOfWeek: DayOfWeek): TimeScheduleDayOfWeekData {
        return TimeScheduleDayOfWeekData(
            dayOfWeek = dayOfWeek,
            uuid = UUID.randomUUID().toString()
        )
    }

    fun getTestScheduleDayOfWeeks1(): List<DayOfWeek> {
        return listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.FRIDAY
        )
    }

    fun getTestScheduleDayOfWeeks2(): List<DayOfWeek> {
        return listOf(
            DayOfWeek.TUESDAY,
            DayOfWeek.THURSDAY,
        )
    }

    fun getTestScheduleEmptyDayOfWeeks(): List<DayOfWeek> {
        return listOf(
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
    }

}