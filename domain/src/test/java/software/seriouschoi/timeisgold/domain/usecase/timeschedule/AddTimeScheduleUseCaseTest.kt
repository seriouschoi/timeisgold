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

/**
 * Created by jhchoi on 2025. 8. 8.
 * jhchoi@neofect.com
 */
@RunWith(MockitoJUnitRunner::class)
internal class AddTimeScheduleUseCaseTest {
    private lateinit var testFixture: TimeScheduleDataFixture

    @Mock
    lateinit var timeScheduleRepo: TimeScheduleRepository

    private lateinit var addTimeScheduleUseCase: AddTimeScheduleUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        testFixture = TimeScheduleDataFixture
        addTimeScheduleUseCase = AddTimeScheduleUseCase(timeScheduleRepo, TimeSchedulePolicy())
    }

    @Test(expected = TIGException.ScheduleConflict::class)
    fun `addTimeSchedule when duplicate dayOfWeek should throw exception`() {
        runTest {
            whenever(timeScheduleRepo.getAllTimeSchedules()).thenReturn(
                listOf(
                    testFixture.createTimeSchedule(listOf(DayOfWeek.SUNDAY)),
                    testFixture.createTimeSchedule(listOf(DayOfWeek.MONDAY)),
                )
            )

            val scheduleInMondayWednesday = testFixture.createTimeSchedule(
                listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
            )
            addTimeScheduleUseCase.invoke(scheduleInMondayWednesday)
        }
    }

    @Test
    fun `addTimeSchedule when not duplicate dayOfWeek should not throw exception`() {
        runTest {
            whenever(timeScheduleRepo.getAllTimeSchedules()).thenReturn(
                listOf(
                    testFixture.createTimeSchedule(listOf(DayOfWeek.SUNDAY)),
                    testFixture.createTimeSchedule(listOf(DayOfWeek.MONDAY)),
                )
            )

            val scheduleInMondayWednesday = testFixture.createTimeSchedule(
                listOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)
            )
            addTimeScheduleUseCase.invoke(scheduleInMondayWednesday)
        }
    }


}