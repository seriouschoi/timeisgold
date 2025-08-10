package software.seriouschoi.timeisgold.domain.usecase.timeschedule

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import software.seriouschoi.timeisgold.domain.exception.TIGException
import software.seriouschoi.timeisgold.domain.fixture.TimeScheduleDataFixture
import software.seriouschoi.timeisgold.domain.policy.TimeSchedulePolicy
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository
import java.time.DayOfWeek

@RunWith(MockitoJUnitRunner::class)
internal class SetTimeScheduleUseCaseTest {
    private lateinit var testFixture: TimeScheduleDataFixture

    @Mock
    lateinit var timeScheduleRepo: TimeScheduleRepository

    private lateinit var useCase: SetTimeScheduleUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        testFixture = TimeScheduleDataFixture
        useCase = SetTimeScheduleUseCase(timeScheduleRepo, TimeSchedulePolicy())

        runTest {
            whenever(timeScheduleRepo.getAllTimeSchedules()).thenReturn(
                listOf(
                    testFixture.createTimeSchedule(listOf(DayOfWeek.SUNDAY)),
                    testFixture.createTimeSchedule(listOf(DayOfWeek.MONDAY)),
                )
            )
        }
    }

    @Test(expected = TIGException.ScheduleConflict::class)
    fun `setTimeSchedule when duplicate dayOfWeek should throw exception`() {
        runTest {
            val scheduleInMondayWednesday = testFixture.createTimeSchedule(
                listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
            )
            useCase.invoke(scheduleInMondayWednesday)
        }
    }

    @Test
    fun `setTimeSchedule when not duplicate dayOfWeek should not throw exception`() {
        runTest {
            val scheduleInMondayWednesday = testFixture.createTimeSchedule(
                listOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)
            )
            useCase.invoke(scheduleInMondayWednesday)
        }
    }
}