package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.flow.flow
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
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import java.time.DayOfWeek

@RunWith(MockitoJUnitRunner::class)
internal class SetTimeRoutineUseCaseTest {
    private val testFixture = TimeRoutineDataFixture()

    @Mock
    lateinit var timeRoutineRepo: TimeRoutineRepositoryPort

    private lateinit var useCase: SetTimeRoutineCompositionUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = SetTimeRoutineCompositionUseCase(timeRoutineRepo, TimeRoutineDomainService(timeRoutineRepo))

        runTest {
            whenever(timeRoutineRepo.getAllDayOfWeeks()).thenReturn(
                flow {
                    emit(
                        listOf(
                            DayOfWeek.SUNDAY,
                            DayOfWeek.MONDAY
                        ),
                    )
                }
            )
        }
    }

    @Test(expected = TIGException.RoutineConflict::class)
    fun `setTimeRoutine when duplicate dayOfWeek should throw exception`() {
        runTest {
            val routineMonTue = testFixture.routineCompoMonTue
            useCase.invoke(routineMonTue)
        }
    }

    @Test
    fun `setTimeRoutine when not duplicate dayOfWeek should not throw exception`() {
        runTest {
            val routineCompoWedThu = testFixture.routineCompoWedThu
            useCase.invoke(routineCompoWedThu)
        }
    }
}