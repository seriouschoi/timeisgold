package software.seriouschoi.timeisgold.data.fixture

import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineData
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDayOfWeekData
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

    fun createTimeRoutine(dayOfWeekList: List<DayOfWeek>): TimeRoutineData {
        val uuid = UUID.randomUUID()
        return TimeRoutineData(
            uuid = uuid.toString(),
            createTime = System.currentTimeMillis(),
            title = "test_routine_$uuid",
            dayOfWeekList = dayOfWeekList.map {
                createTimeRoutineDayOfWeek(it)
            }
        )
    }

    private fun createTimeRoutineDayOfWeek(dayOfWeek: DayOfWeek): TimeRoutineDayOfWeekData {
        return TimeRoutineDayOfWeekData(
            dayOfWeek = dayOfWeek,
            uuid = UUID.randomUUID().toString()
        )
    }

    fun getTestRoutineDayOfWeeks1(): List<DayOfWeek> {
        return listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.FRIDAY
        )
    }

    fun getTestRoutineDayOfWeeks2(): List<DayOfWeek> {
        return listOf(
            DayOfWeek.TUESDAY,
            DayOfWeek.THURSDAY,
        )
    }

}