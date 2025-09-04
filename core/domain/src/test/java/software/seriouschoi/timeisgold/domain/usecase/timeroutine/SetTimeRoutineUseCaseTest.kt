package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.fixture.FakeTimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.fixture.TimeRoutineDataFixture
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 9. 4.
 * jhchoi
 */
@RunWith(MockitoJUnitRunner::class)
class SetTimeRoutineUseCaseTest {
    private val testFixture = TimeRoutineDataFixture()

    private val timeRoutineRepo: TimeRoutineRepositoryPort = FakeTimeRoutineRepositoryPort(
        listOf(
            testFixture.routineCompoMonTue
        )
    )

    private lateinit var useCase: SetTimeRoutineUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = SetTimeRoutineUseCase(timeRoutineRepo, TimeRoutineDomainService(timeRoutineRepo))
    }

    @Test
    fun `setTimeRoutine when duplicate dayOfWeek should throw exception`() {
        runTest {
            val routineMonTue = testFixture.routineCompoMonTue.timeRoutine
            val testResult = useCase.invoke(routineMonTue, listOf(DayOfWeek.MONDAY))
            assert(testResult is DomainResult.Failure)
        }
    }

    @Test
    fun `setTimeRoutine when not duplicate dayOfWeek should not throw exception`() {
        runTest {
            val routineWedThu = testFixture.routineCompoWedThu.timeRoutine
            val testResult = useCase.invoke(routineWedThu, listOf(DayOfWeek.WEDNESDAY))
            assert(testResult is DomainResult.Success)
        }
    }
}