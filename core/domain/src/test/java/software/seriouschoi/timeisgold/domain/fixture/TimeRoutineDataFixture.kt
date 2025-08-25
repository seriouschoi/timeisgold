package software.seriouschoi.timeisgold.domain.fixture

import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineData
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDayOfWeekData
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDetailData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotData
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID

/**
 * Created by jhchoi on 2025. 8. 8.
 * jhchoi
 */
internal object TimeRoutineDataFixture {
    fun createTimeRoutineDetail(
        dayOfWeekList: List<DayOfWeek>,
    ): TimeRoutineDetailData {
        return TimeRoutineDetailData(
            timeRoutineData = createTimeRoutine(dayOfWeekList),
            timeSlotList = createTimeSlotList()
        )
    }

    fun createTimeRoutine(dayOfWeekList: List<DayOfWeek>): TimeRoutineData {
        val uuid = UUID.randomUUID().toString()
        return TimeRoutineData(
            title = "test_$uuid",
            uuid = uuid,
            createTime = System.currentTimeMillis(),
            dayOfWeekList = dayOfWeekList.map {
                TimeRoutineDayOfWeekData(
                    dayOfWeek = it,
                    uuid = UUID.randomUUID().toString()
                )
            }
        )
    }

    private fun createTimeSlotList(): List<TimeSlotData> {
        val baseTime = LocalTime.of(10, 0, 0)

        return (0..10).map { i ->
            val startTime = baseTime.plusHours(i.toLong())
            val endTime = startTime.plusHours(1)

            val uuid = UUID.randomUUID().toString()
            TimeSlotData(
                uuid = uuid,
                title = "test_$uuid",
                startTime = startTime,
                endTime = endTime,
                createTime = System.currentTimeMillis()
            )
        }
    }
}