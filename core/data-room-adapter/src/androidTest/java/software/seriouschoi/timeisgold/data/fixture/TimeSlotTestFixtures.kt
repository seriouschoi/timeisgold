package software.seriouschoi.timeisgold.data.fixture

import software.seriouschoi.timeisgold.domain.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.entities.TimeSlotEntity
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID

internal object TimeSlotTestFixtures {
    fun createDetailDataList(): List<TimeSlotComposition> {
        return (0..10).map { i ->
            return@map createDetailTimeSlot()
        }
    }

    fun createDetailTimeSlot(): TimeSlotComposition {
        val uuid = UUID.randomUUID()
        val now = LocalTime.now()
        val timeSlotData = TimeSlotEntity(
            uuid = uuid.toString(),
            title = "test_$uuid",
            startTime = now.minusMinutes(10),
            endTime = now,
            createTime = System.currentTimeMillis()
        )

        return TimeSlotComposition(
            timeSlotData = timeSlotData,
        )
    }

    fun createTimeRoutine(dayOfWeekList: List<DayOfWeek>): TimeRoutineComposition {
        val uuid = UUID.randomUUID().toString()
        val timeRoutine = TimeRoutineEntity(
            uuid = uuid,
            title = "test_routine_$uuid",
            createTime = System.currentTimeMillis()
        )
        val dayOfWeekEntities = dayOfWeekList.map {
            createTimeRoutineDayOfWeek(it)
        }
        return TimeRoutineComposition(
            timeRoutine = timeRoutine,
            timeSlots = emptyList(),
            dayOfWeeks = dayOfWeekEntities
        )
    }

    private fun createTimeRoutineDayOfWeek(dayOfWeek: DayOfWeek): TimeRoutineDayOfWeekEntity {
        return TimeRoutineDayOfWeekEntity(
            dayOfWeek = dayOfWeek,
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