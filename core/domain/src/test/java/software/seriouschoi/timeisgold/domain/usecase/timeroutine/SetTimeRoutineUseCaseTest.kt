package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import software.seriouschoi.timeisgold.core.test.util.FakeTimeRoutineRepositoryAdapter
import software.seriouschoi.timeisgold.core.test.util.TimeRoutineTestFixtures
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeRoutineDomainService
import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 9. 4.
 * jhchoi
 */
@RunWith(MockitoJUnitRunner::class)
class SetTimeRoutineUseCaseTest {
    private val testFixture = TimeRoutineTestFixtures()

    private val timeRoutineRepo: TimeRoutineRepositoryPort = FakeTimeRoutineRepositoryAdapter(
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
            val dayOfWeeks = testFixture.routineCompoMonTue.dayOfWeeks
            val testResult = useCase.invoke(
                TimeRoutineDefinition(
                    timeRoutine = routineMonTue,
                    dayOfWeeks = dayOfWeeks
                )
            )
            assert(testResult is DomainResult.Failure)
        }
    }

    @Test
    fun `setTimeRoutine when not duplicate dayOfWeek should not throw exception`() {
        runTest {
            val routineWedThu = testFixture.routineCompoWedThu.timeRoutine
            val dayOfWeeks = testFixture.routineCompoWedThu.dayOfWeeks
            val testResult = useCase.invoke(
                TimeRoutineDefinition(
                    timeRoutine = routineWedThu,
                    dayOfWeeks = dayOfWeeks
                )
            )
            assert(testResult is DomainResult.Success)
        }
    }
}