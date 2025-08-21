package software.seriouschoi.timeisgold.data.repositories.timeroutine

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import java.time.DayOfWeek

@RunWith(AndroidJUnit4::class)
internal class AddTimeRoutineTest : BaseRoomTest() {

    @Test
    fun addTimeRoutine_whenQueriedOnCorrectDay_shouldReturnEntity() {
        //time routine이 정상적으로 삽입되는가?
        runTest {
            val routine = timeSlotTestFixtures.createTimeRoutine(
                listOf(DayOfWeek.SUNDAY)
            )
            timeRoutineRepo.addTimeRoutine(routine)

            val routineFromDb = timeRoutineRepo.getTimeRoutineDetail(DayOfWeek.SUNDAY)
            assert(routineFromDb?.timeRoutineData == routine)
        }
    }

    @Test
    fun addTimeRoutine_whenQueriedOnAnotherDay_shouldAnotherDay() {
        //TimeRoutine을 넣었을때, 정확한 요일로 조회가 되는가?
        runTest {
            val routine1 = timeSlotTestFixtures.createTimeRoutine(
                listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
            )
            timeRoutineRepo.addTimeRoutine(routine1)

            val routine2 = timeSlotTestFixtures.createTimeRoutine(
                listOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)
            )
            timeRoutineRepo.addTimeRoutine(routine2)

            val routineFromDb = timeRoutineRepo.getTimeRoutineDetail(DayOfWeek.TUESDAY)
            assert(routine1 != routineFromDb?.timeRoutineData)
            assert(routine2 == routineFromDb?.timeRoutineData)

            val routineFromDb2 = timeRoutineRepo.getTimeRoutineDetail(DayOfWeek.SATURDAY)
            assert(routineFromDb2 == null)
        }
    }

    @Test(expected = Exception::class)
    fun addTimeRoutine_duplicateUuid_shouldThrowException() {
        //중복된 uuid를 추가할때 예외가 발생하는가?
        runTest {
            val routine1 = timeSlotTestFixtures.createTimeRoutine(
                listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
            )
            timeRoutineRepo.addTimeRoutine(routine1)

            val routine2 = timeSlotTestFixtures.createTimeRoutine(
                listOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)
            ).copy(
                uuid = routine1.uuid
            )
            timeRoutineRepo.addTimeRoutine(routine2)
        }
    }

    @Test(expected = Exception::class)
    fun addTimeRoutine_duplicateDayOfWeekUuid_shouldThrowException() {
        runTest {
            val routine1 = timeSlotTestFixtures.createTimeRoutine(
                listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
            )
            timeRoutineRepo.addTimeRoutine(routine1)

            val routine2 = timeSlotTestFixtures.createTimeRoutine(
                listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
            ).copy(
                dayOfWeekList = routine1.dayOfWeekList
            )

            timeRoutineRepo.addTimeRoutine(routine2)
        }
    }

}