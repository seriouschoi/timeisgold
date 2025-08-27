package software.seriouschoi.timeisgold.data.repositories.timeroutine

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import java.time.DayOfWeek
import kotlin.test.Test

/**
 * Created by jhchoi on 2025. 8. 8.
 * jhchoi
 */
@RunWith(AndroidJUnit4::class)
internal class GetTimeRoutineTest : BaseRoomTest() {
    @Test
    fun getTimeRoutine_whenEmptyRoutine_returnNull() {
        runTest {
            val routine = timeRoutineRepo.getTimeRoutineDetail(DayOfWeek.MONDAY)
            assert(routine == null)
        }
    }

    @Test
    fun getTimeRoutine_shouldReturnData() {
        runTest {
            val routineForAdd = timeSlotTestFixtures.createTimeRoutine(
                timeSlotTestFixtures.getTestRoutineDayOfWeeks1()
            )
            timeRoutineRepo.addTimeRoutine(routineForAdd)

            val routine = timeRoutineRepo.getTimeRoutineDetail(
                timeSlotTestFixtures.getTestRoutineDayOfWeeks1().first()
            )
            assert(routine?.timeRoutineData == routineForAdd)
        }
    }

    @Test
    fun getAllTimeRoutines_whenEmpty_returnEmptyList() {
        runTest {
            val allRoutineList = timeRoutineRepo.getAllTimeRoutines()
            assert(allRoutineList.isEmpty())
        }
    }
}