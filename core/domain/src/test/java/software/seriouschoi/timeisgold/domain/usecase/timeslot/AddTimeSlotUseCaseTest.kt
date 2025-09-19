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
import software.seriouschoi.timeisgold.core.test.util.TimeRoutineTestFixtures
import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.exception.TIGException
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeSlotDomainService
import java.time.LocalTime
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class AddTimeSlotUseCaseTest {
    private val testFixture = TimeRoutineTestFixtures()

    @Mock
    lateinit var timeRoutineRepo: TimeRoutineRepositoryPort

    @Mock
    lateinit var timeSlotRepo: TimeSlotRepositoryPort

    private lateinit var useCase: AddTimeSlotUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = AddTimeSlotUseCase(
            timeSlotRepo = timeSlotRepo,
            timeSlotDomainService = TimeSlotDomainService(timeSlotRepo),
        )
    }

    @Test(expected = TIGException.TimeSlotConflict::class)
    fun `addTimeSlot when same time should throw exception`() {
        runTest {
            val routineCompo = testFixture.routineCompoMonTue.copy(
                timeSlots = testFixture.generateTimeSlotList(
                    18, 21
                )
            )
            val routineUuid = routineCompo.timeRoutine.uuid
            whenever(timeSlotRepo.watchTimeSlotList(routineUuid)).thenReturn(
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
    fun `addTimeSlot when overlap time should throw exception`() {
        runTest {
            val routineCompo = testFixture.routineCompoMonTue.copy(
                timeSlots = testFixture.generateTimeSlotList(
                    18, 21
                )
            )
            val routineUuid = routineCompo.timeRoutine.uuid
            whenever(timeSlotRepo.watchTimeSlotList(routineUuid)).thenReturn(
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