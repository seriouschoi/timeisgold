package software.seriouschoi.timeisgold.data.repositories.timeroutine

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDetailData
import java.time.DayOfWeek
import kotlin.test.Test
import kotlin.test.todo

@RunWith(AndroidJUnit4::class)
internal class GetTimeRoutineFlowTest : BaseRoomTest() {
    @Test
    fun getTimeRoutine_whenEmptyRoutine_returnNull() {
        runTest {
            val testDayOfWeeks = timeSlotTestFixtures.getTestRoutineDayOfWeeks1()
            timeRoutineRepo.getTimeRoutineDetailFlow(testDayOfWeeks.first()).test {
                assert(awaitItem() == null)
            }
        }
    }

    @Test
    fun getTimeRoutine_shouldReturnData() {
        runTest {
            val testDayOfWeeks = timeSlotTestFixtures.getTestRoutineDayOfWeeks1()
            val routineForAdd = timeSlotTestFixtures.createTimeRoutine(
                dayOfWeekList = testDayOfWeeks
            )

            timeRoutineRepo.getTimeRoutineDetailFlow(
                week = testDayOfWeeks.first()
            ).test {
                timeRoutineRepo.addTimeRoutine(routineForAdd)

                assert(awaitItem()?.timeRoutineData == routineForAdd)
            }
        }
    }

    /**
     * 루틴을 추가하고,
     * 해당 루틴을 요일로 stream을 하는 상태에서,
     * 값이 바뀌었을때, 바뀐 값을 수신하는가?
     */
    @Test
    fun getTimeRoutine_changeRoutine_returnChangedRoutine() {
        runTest {
            val testDayOfWeeks = timeSlotTestFixtures.getTestRoutineDayOfWeeks1()
            val routineForAdd = timeSlotTestFixtures.createTimeRoutine(
                dayOfWeekList = testDayOfWeeks
            )

            val routineForAdd2 = timeSlotTestFixtures.createTimeRoutine(
                dayOfWeekList = listOf(testDayOfWeeks.first())
            )

            timeRoutineRepo.getTimeRoutineDetailFlow(
                week = testDayOfWeeks.first()
            ).test {
                timeRoutineRepo.addTimeRoutine(routineForAdd)
                assert(awaitItem()?.timeRoutineData == routineForAdd)

                timeRoutineRepo.addTimeRoutine(routineForAdd2)
                val changedRoutine = awaitItem()
                assert(changedRoutine?.timeRoutineData != routineForAdd)
                assert(changedRoutine?.timeRoutineData == routineForAdd2)
            }
        }
    }
}