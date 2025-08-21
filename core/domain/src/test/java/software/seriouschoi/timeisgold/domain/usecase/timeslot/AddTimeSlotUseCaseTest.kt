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
import software.seriouschoi.timeisgold.domain.fixture.TimeRoutineDataFixture
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import java.time.DayOfWeek
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class AddTimeSlotUseCaseTest {
    private lateinit var testFixture: TimeRoutineDataFixture

    @Mock
    lateinit var timeRoutineRepo: TimeRoutineRepositoryPort

    @Mock
    lateinit var timeSlotRepo: TimeSlotRepositoryPort

    private lateinit var useCase: AddTimeSlotUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        testFixture = TimeRoutineDataFixture
        useCase = AddTimeSlotUseCase(
            timeRoutineRepositoryPort = timeRoutineRepo,
            timeslotRepositoryPort = timeSlotRepo,
            timeslotPolicy = TimeSlotPolicy()
        )
    }

    @Test(expected = TIGException.TimeSlotConflict::class)
    fun `addTimeSlot when same time should throw exception`() {
        runTest {
            val routineUuid = UUID.randomUUID().toString()
            whenever(timeRoutineRepo.getTimeRoutineDetailByUuid(routineUuid)).thenReturn(
                testFixture.createTimeRoutineDetail(listOf(DayOfWeek.MONDAY))
            )

            val routine = timeRoutineRepo.getTimeRoutineDetailByUuid(routineUuid)
                ?: throw IllegalStateException("time routine not found")
            val timeslotFromData = routine.timeSlotList.first()

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

            useCase.invoke(routineUuid, timeSlotDetailForAdd)
        }
    }

    @Test(expected = TIGException.TimeSlotConflict::class)
    fun `addTimeSlot when overlap time should throw exception`() {
        runTest {
            val routineUuid = UUID.randomUUID().toString()
            whenever(timeRoutineRepo.getTimeRoutineDetailByUuid(routineUuid)).thenReturn(
                testFixture.createTimeRoutineDetail(listOf(DayOfWeek.MONDAY))
            )

            val routine = timeRoutineRepo.getTimeRoutineDetailByUuid(routineUuid)
                ?: throw IllegalStateException("time routine not found")
            val timeslotFromData = routine.timeSlotList.last()

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

            useCase.invoke(routineUuid, timeSlotDetailForAdd)
        }
    }
}