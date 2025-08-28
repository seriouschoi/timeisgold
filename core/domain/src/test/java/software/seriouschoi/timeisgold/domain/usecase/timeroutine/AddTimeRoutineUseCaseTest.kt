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
import software.seriouschoi.timeisgold.domain.policy.TimeRoutinePolicy
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 8. 8.
 * jhchoi
 */
@RunWith(MockitoJUnitRunner::class)
internal class AddTimeRoutineUseCaseTest {
    private val testFixture = TimeRoutineDataFixture()

    @Mock
    lateinit var timeRoutineRepo: TimeRoutineRepositoryPort

    private lateinit var useCase: AddTimeRoutineUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = AddTimeRoutineUseCase(timeRoutineRepo, TimeRoutinePolicy())

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
    fun `addTimeRoutine when duplicate dayOfWeek should throw exception`() {
        runTest {
            val routineMonTue = testFixture.routineCompoMonTue
            useCase.invoke(routineMonTue)
        }
    }

    @Test
    fun `addTimeRoutine when not duplicate dayOfWeek should not throw exception`() {
        runTest {
            val routineMonTue = testFixture.routineCompoWedThu
            useCase.invoke(routineMonTue)
        }
    }


}