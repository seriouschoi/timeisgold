package software.seriouschoi.timeisgold.data.repositories.timeschedule

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import java.time.DayOfWeek
import kotlin.test.Test

/**
 * Created by jhchoi on 2025. 8. 8.
 * jhchoi@neofect.com
 */
@RunWith(AndroidJUnit4::class)
internal class GetTimeScheduleTest : BaseRoomTest() {
    @Test
    fun getTimeSchedule_whenEmptySchedule_returnNull() {
        runTest {
            val schedule = timeScheduleRepo.getTimeScheduleDetail(DayOfWeek.MONDAY)
            assert(schedule == null)
        }
    }

    @Test
    fun getTimeSchedule_shouldReturnData() {
        runTest {
            val scheduleForAdd = timeSlotTestFixtures.createTimeSchedule(
                timeSlotTestFixtures.getTestScheduleDayOfWeeks1()
            )
            timeScheduleRepo.addTimeSchedule(scheduleForAdd)

            val schedule = timeScheduleRepo.getTimeScheduleDetail(DayOfWeek.MONDAY)
            assert(schedule?.timeScheduleData == scheduleForAdd)
        }
    }

    @Test
    fun getAllTimeSchedules_whenEmpty_returnEmptyList() {
        runTest {
            val allScheduleList = timeScheduleRepo.getAllTimeSchedules()
            assert(allScheduleList.isEmpty())
        }
    }
}