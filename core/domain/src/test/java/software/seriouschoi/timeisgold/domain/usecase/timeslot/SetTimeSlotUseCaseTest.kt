package software.seriouschoi.timeisgold.domain.usecase.timeslot

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.exception.TIGException
import software.seriouschoi.timeisgold.domain.fixture.TimeRoutineDataFixture
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeSlotDomainService
import java.time.LocalTime
import java.util.UUID


@RunWith(MockitoJUnitRunner::class)
class SetTimeSlotUseCaseTest {
    private val testFixture = TimeRoutineDataFixture()

    @Mock
    lateinit var timeRoutineRepo: TimeRoutineRepositoryPort

    @Mock
    lateinit var timeSlotRepo: TimeSlotRepositoryPort

    private lateinit var useCase: SetTimeSlotUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = SetTimeSlotUseCase(
            timeslotRepositoryPort = timeSlotRepo,
            timeSlotDomainService = TimeSlotDomainService(timeSlotRepo)
        )
    }

    @Test(expected = TIGException.TimeSlotConflict::class)
    fun `setTimeSlot when same time should throw exception`() {
        runTest {
            val routineCompo = testFixture.routineCompoMonTue.copy(
                timeSlots = testFixture.generateTimeSlotList(
                    18, 21
                )
            )
            val routineUuid = routineCompo.timeRoutine.uuid
            whenever(timeSlotRepo.observeTimeSlotList(routineUuid)).thenReturn(
                flow {
                    emit(testFixture.routineCompoMonTue.timeSlots)
                }
            )

            val timeSlotForAdd = TimeSlotEntity(
                uuid = UUID.randomUUID().toString(),
                title = "test",
                startTime = LocalTime.of(18, 0),
                endTime = LocalTime.of(19, 0),
                createTime = System.currentTimeMillis(),
            )
            val timeSlotDetailForAdd = TimeSlotComposition(
                timeSlotData = timeSlotForAdd,
            )

            useCase.invoke(routineUuid, timeSlotDetailForAdd)
        }
    }

    @Test(expected = TIGException.TimeSlotConflict::class)
    fun `setTimeSlot when overlap time should throw exception`() {
        runTest {
            val routineCompo = testFixture.routineCompoMonTue.copy(
                timeSlots = testFixture.generateTimeSlotList(
                    18, 21
                )
            )
            val routineUuid = routineCompo.timeRoutine.uuid
            whenever(timeSlotRepo.observeTimeSlotList(routineUuid)).thenReturn(
                flow {
                    emit(testFixture.routineCompoMonTue.timeSlots)
                }
            )

            val timeSlotForAdd = TimeSlotEntity(
                uuid = UUID.randomUUID().toString(),
                title = "test",
                startTime = LocalTime.of(20, 50),
                endTime = LocalTime.of(21, 10),
                createTime = System.currentTimeMillis(),
            )
            val timeSlotDetailForAdd = TimeSlotComposition(
                timeSlotData = timeSlotForAdd,
            )

            useCase.invoke(routineUuid, timeSlotDetailForAdd)
        }
    }
}