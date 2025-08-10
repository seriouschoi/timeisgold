package software.seriouschoi.timeisgold.domain.usecase.timeslot

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.exception.TIGException
import software.seriouschoi.timeisgold.domain.fixture.TimeScheduleDataFixture
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository
import java.time.DayOfWeek
import java.util.UUID


@RunWith(MockitoJUnitRunner::class)
class SetTimeSlotUseCaseTest {
    private lateinit var testFixture: TimeScheduleDataFixture

    @Mock
    lateinit var timeScheduleRepo: TimeScheduleRepository

    @Mock
    lateinit var timeSlotRepo: TimeSlotRepository

    private lateinit var useCase: SetTimeSlotUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        testFixture = TimeScheduleDataFixture
        useCase = SetTimeSlotUseCase(
            timeScheduleRepository = timeScheduleRepo,
            timeslotRepository = timeSlotRepo,
            timeslotPolicy = TimeSlotPolicy()
        )
    }

    @Test(expected = TIGException.TimeSlotConflict::class)
    fun `setTimeSlot when same time should throw exception`() {
        runTest {
            val scheduleUuid = UUID.randomUUID().toString()
            whenever(timeScheduleRepo.getTimeScheduleDetailByUuid(scheduleUuid)).thenReturn(
                testFixture.createTimeScheduleDetail(listOf(DayOfWeek.MONDAY))
            )

            val schedule = timeScheduleRepo.getTimeScheduleDetailByUuid(scheduleUuid)
                ?: throw IllegalStateException("time schedule not found")
            val timeslotFromData = schedule.timeSlotList.first()

            val timeSlotForAdd = TimeSlotData(
                uuid = UUID.randomUUID().toString(),
                title = "test",
                startTime = timeslotFromData.startTime,
                endTime = timeslotFromData.endTime,
                createTime = System.currentTimeMillis(),
            )
            val timeSlotDetailForAdd = TimeSlotDetailData(
                timeSlotData = timeSlotForAdd,
                timeSlotMemoData = null
            )

            useCase(scheduleUuid, timeSlotDetailForAdd)
        }
    }

    @Test(expected = TIGException.TimeSlotConflict::class)
    fun `setTimeSlot when overlap time should throw exception`() {
        runTest {
            val scheduleUuid = UUID.randomUUID().toString()
            whenever(timeScheduleRepo.getTimeScheduleDetailByUuid(scheduleUuid)).thenReturn(
                testFixture.createTimeScheduleDetail(listOf(DayOfWeek.MONDAY))
            )

            val schedule = timeScheduleRepo.getTimeScheduleDetailByUuid(scheduleUuid)
                ?: throw IllegalStateException("time schedule not found")
            val timeslotFromData = schedule.timeSlotList.last()

            val timeSlotForAdd = TimeSlotData(
                uuid = UUID.randomUUID().toString(),
                title = "test",
                startTime = timeslotFromData.endTime.minusMinutes(10),
                endTime = timeslotFromData.endTime.plusMinutes(10),
                createTime = System.currentTimeMillis(),
            )
            val timeSlotDetailForAdd = TimeSlotDetailData(
                timeSlotData = timeSlotForAdd,
                timeSlotMemoData = null
            )

            useCase(scheduleUuid, timeSlotDetailForAdd)
        }
    }
}