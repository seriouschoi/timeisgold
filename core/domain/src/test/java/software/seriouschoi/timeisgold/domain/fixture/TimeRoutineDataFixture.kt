package software.seriouschoi.timeisgold.domain.fixture

import software.seriouschoi.software.seriouschoi.util.localtime.toEpochMillis
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

/**
 * Created by jhchoi on 2025. 8. 8.
 * jhchoi
 */
internal class TimeRoutineDataFixture {
    // TODO: :core:data의 테스트 모듈과 병합하기.
    val routineCompoMonTue: TimeRoutineComposition = TimeRoutineComposition(
        timeRoutine = generateTimeRoutine(
            routineTitle = "routine1",
            createDayAgo = 10
        ),
        timeSlots = generateTimeSlotList(),
        dayOfWeeks = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY
        ).map {
            TimeRoutineDayOfWeekEntity(it)
        }.toSet()
    )

    val routineCompoWedThu: TimeRoutineComposition = TimeRoutineComposition(
        timeRoutine = generateTimeRoutine(
            routineTitle = "routine2",
            createDayAgo = 5
        ),
        timeSlots = generateTimeSlotList(),
        dayOfWeeks = listOf(
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY
        ).map {
            TimeRoutineDayOfWeekEntity(it)
        }.toSet()
    )

    val routineCompoSun: TimeRoutineComposition = TimeRoutineComposition(
        timeRoutine = generateTimeRoutine(
            routineTitle = "routine3",
            createDayAgo = 2
        ),
        timeSlots = generateTimeSlotList(),
        dayOfWeeks = listOf(
            DayOfWeek.SUNDAY,
        ).map {
            TimeRoutineDayOfWeekEntity(it)
        }.toSet()
    )


    fun generateTimeRoutine(
        routineTitle: String,
        createDayAgo: Long = 0
    ): TimeRoutineEntity {
        return TimeRoutineEntity(
            title = routineTitle,
            uuid = UUID.randomUUID().toString(),
            createTime = LocalDateTime.now().minusDays(createDayAgo).toEpochMillis()
        )
    }


    fun generateTimeSlotList(startHour: Int = 0, endHour: Int = 23): List<TimeSlotEntity> {
        val timeSlotStartTime = LocalTime.of(0, 0)

        return (startHour..endHour).map { i ->
            val uuid = UUID.randomUUID()
            TimeSlotEntity(
                uuid = uuid.toString(),
                title = "test_$uuid",
                startTime = timeSlotStartTime.plusHours(i.toLong()),
                endTime = timeSlotStartTime.plusHours(i.toLong() + 1),
                createTime = LocalDateTime.now().toEpochMillis()
            )
        }
    }

    fun generateTimeSlotCompositionList(startHour: Int = 0, endHour: Int = 23): List<TimeSlotComposition> {
        return generateTimeSlotList(startHour, endHour).map {
            TimeSlotComposition(it)
        }
    }
}