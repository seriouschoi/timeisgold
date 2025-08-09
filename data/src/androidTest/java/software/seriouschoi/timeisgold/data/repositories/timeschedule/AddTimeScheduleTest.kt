package software.seriouschoi.timeisgold.data.repositories.timeschedule

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import java.time.DayOfWeek

@RunWith(AndroidJUnit4::class)
internal class AddTimeScheduleTest : BaseRoomTest() {

    @Test
    fun addTimeSchedule_whenQueriedOnCorrectDay_shouldReturnEntity() {
        //time schedule가 정상적으로 삽입되는가?
        runTest {
            val schedule = timeSlotTestFixtures.createTimeSchedule(
                listOf(DayOfWeek.SUNDAY)
            )
            timeScheduleRepo.addTimeSchedule(schedule)

            val scheduleFromDb = timeScheduleRepo.getTimeSchedule(DayOfWeek.SUNDAY)
            assert(scheduleFromDb?.timeScheduleData == schedule)
        }
    }

    @Test
    fun addTimeSchedule_whenQueriedOnAnotherDay_shouldAnotherDay() {
        //TimeSchedule을 넣었을때, 정확한 요일로 조회가 되는가?
        runTest {
            val schedule1 = timeSlotTestFixtures.createTimeSchedule(
                listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
            )
            timeScheduleRepo.addTimeSchedule(schedule1)

            val schedule2 = timeSlotTestFixtures.createTimeSchedule(
                listOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)
            )
            timeScheduleRepo.addTimeSchedule(schedule2)

            val scheduleFromDb = timeScheduleRepo.getTimeSchedule(DayOfWeek.TUESDAY)
            assert(schedule1 != scheduleFromDb?.timeScheduleData)
            assert(schedule2 == scheduleFromDb?.timeScheduleData)

            val scheduleFromDb2 = timeScheduleRepo.getTimeSchedule(DayOfWeek.SATURDAY)
            assert(scheduleFromDb2 == null)
        }
    }

    @Test(expected = Exception::class)
    fun addTimeSchedule_duplicateUuid_shouldThrowException() {
        //중복된 uuid를 추가할때 예외가 발생하는가?
        runTest {
            val schedule1 = timeSlotTestFixtures.createTimeSchedule(
                listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
            )
            timeScheduleRepo.addTimeSchedule(schedule1)

            val schedule2 = timeSlotTestFixtures.createTimeSchedule(
                listOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)
            ).copy(
                uuid = schedule1.uuid
            )
            timeScheduleRepo.addTimeSchedule(schedule2)
        }
    }

    @Test(expected = Exception::class)
    fun addTimeSchedule_duplicateDayOfWeekUuid_shouldThrowException() {
        runTest {
            val schedule1 = timeSlotTestFixtures.createTimeSchedule(
                listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
            )
            timeScheduleRepo.addTimeSchedule(schedule1)

            val schedule2 = timeSlotTestFixtures.createTimeSchedule(
                listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
            ).copy(
                dayOfWeekList = schedule1.dayOfWeekList
            )

            timeScheduleRepo.addTimeSchedule(schedule2)
        }
    }

}