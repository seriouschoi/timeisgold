package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import software.seriouschoi.timeisgold.domain.exception.TIGException
import software.seriouschoi.timeisgold.domain.fixture.TimeRoutineDataFixture
import software.seriouschoi.timeisgold.domain.policy.TimeRoutinePolicy
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek

@RunWith(MockitoJUnitRunner::class)
internal class SetTimeRoutineUseCaseTest {
    private lateinit var testFixture: TimeRoutineDataFixture

    @Mock
    lateinit var timeRoutineRepo: TimeRoutineRepositoryPort

    private lateinit var useCase: SetTimeRoutineUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        testFixture = TimeRoutineDataFixture
        useCase = SetTimeRoutineUseCase(timeRoutineRepo, TimeRoutinePolicy())

        runTest {
            whenever(timeRoutineRepo.getAllTimeRoutines()).thenReturn(
                listOf(
                    testFixture.createTimeRoutine(listOf(DayOfWeek.SUNDAY)),
                    testFixture.createTimeRoutine(listOf(DayOfWeek.MONDAY)),
                )
            )
        }
    }

    @Test(expected = TIGException.RoutineConflict::class)
    fun `setTimeRoutine when duplicate dayOfWeek should throw exception`() {
        runTest {
            val routineInMondayWednesday = testFixture.createTimeRoutine(
                listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
            )
            useCase.invoke(routineInMondayWednesday)
        }
    }

    @Test
    fun `setTimeRoutine when not duplicate dayOfWeek should not throw exception`() {
        runTest {
            val routineInMondayWednesday = testFixture.createTimeRoutine(
                listOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)
            )
            useCase.invoke(routineInMondayWednesday)
        }
    }
}