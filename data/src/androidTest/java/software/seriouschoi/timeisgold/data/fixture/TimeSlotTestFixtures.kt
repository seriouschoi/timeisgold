package software.seriouschoi.timeisgold.data.fixture

import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleDayOfWeekData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotMemoData
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID
import kotlin.random.Random

object TimeSlotTestFixtures {
    fun createDetailDataList(): List<TimeSlotDetailData> {
        return (0..10).map { i ->
            val uuid = UUID.randomUUID()
            val memoUuid = UUID.randomUUID()
            val createTime = System.currentTimeMillis() - (i * 1000 * 60)
            val timeSlotData = TimeSlotData(
                uuid = uuid.toString(),
                title = "test-$uuid",
                startTime = LocalTime.now(),
                endTime = LocalTime.now(),
                createTime = createTime
            )
            val timeSlotMemoData = TimeSlotMemoData(
                uuid = memoUuid.toString(),
                memo = "test-$memoUuid",
                createTime = createTime
            )
            return@map TimeSlotDetailData(
                timeSlotData = timeSlotData,
                timeSlotMemoData = timeSlotMemoData
            )
        }
    }

    fun createDetailTimeSlot(): TimeSlotDetailData {
        val uuid = UUID.randomUUID()
        val memoUuid = UUID.randomUUID()
        val timeSlotData = TimeSlotData(
            uuid = uuid.toString(),
            title = "test_$uuid",
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
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

}