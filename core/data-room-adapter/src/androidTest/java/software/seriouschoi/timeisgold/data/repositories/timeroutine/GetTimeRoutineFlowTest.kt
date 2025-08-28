package software.seriouschoi.timeisgold.data.repositories.timeroutine

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
internal class GetTimeRoutineFlowTest : BaseRoomTest() {
    /**
     * 데이터가 없는 상태에서 오류 없이
     * 타임루틴을 요청하면 null로 리턴 되는가?
     */
    @Test
    fun givenEmptyRepo_whenQuery_thenReturnNull() {
        runTest {
            val testDay =
                testFixtures.routineCompoMonTue.dayOfWeeks.first().dayOfWeek
            timeRoutineRepo.getTimeRoutineByDayOfWeek(testDay).test {
                assertNull(awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    /**
     * 저장된 타임루틴이 정상적으로 불러와지는가?
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun givenRoutine_whenQuery_returnRoutine() = runTest {
        turbineScope {
            val routine1 = testFixtures.routineCompoMonTue
            val days = routine1.dayOfWeeks.map { it.dayOfWeek }

            val dayRoutineTurbine = timeRoutineRepo.getTimeRoutineByDayOfWeek(days.first()).testIn(
                backgroundScope
            )

            backgroundScope.launch {
                timeRoutineRepo.addTimeRoutineComposition(routine1)
            }

            advanceUntilIdle()

            assert(dayRoutineTurbine.awaitItem() == routine1)
            dayRoutineTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * 루틴을 추가하고,
     * 요일을 조건으로 루틴을 스트림하는 상태에서,
     * 요일에 해당되는 루틴을 바꿨을때,
     * 바뀐 루틴을 수신하는가?
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun givenDayRoutine_whenChangeRoutineWhereDayOfWeek_returnChangedRoutine() = runTest {
        turbineScope {
            val routineForAdd1 = testFixtures.routineCompoMonTue
            val routineForAdd2 = testFixtures.routineCompoWedThu.copy(
                dayOfWeeks = routineForAdd1.dayOfWeeks
            )

            val routine1Day = routineForAdd1.dayOfWeeks.first().dayOfWeek

            val routine1Turbine = timeRoutineRepo.getTimeRoutineByDayOfWeek(routine1Day).testIn(
                backgroundScope
            )

            backgroundScope.launch {
                timeRoutineRepo.addTimeRoutineComposition(routineForAdd1)
            }
            advanceUntilIdle()

            assert(routine1Turbine.awaitItem() == routineForAdd1)

            backgroundScope.launch {
                timeRoutineRepo.addTimeRoutineComposition(routineForAdd2)
            }
            advanceUntilIdle()
            assert(routine1Turbine.awaitItem() == routineForAdd2)


            routine1Turbine.cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * 요일로 타임루틴이 조회가 되는가?
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getTimeRoutineCompositionByDayOfWeek_returnsOnlyMatchingDay() = runTest {
        turbineScope {
            //TimeRoutine을 넣었을때, 정확한 요일로 조회가 되는가?
            val routine1Composition = testFixtures.routineCompoMonTue
            val routine2Composition = testFixtures.routineCompoWedThu
            val routine2Days = routine2Composition.dayOfWeeks.map { it.dayOfWeek }
            val routine3Composition = testFixtures.routineCompoSun
            val routine3Days = routine3Composition.dayOfWeeks.map { it.dayOfWeek }

            val routine2DayTurbine = timeRoutineRepo
                .getTimeRoutineCompositionByDayOfWeek(routine2Days.first())
                .filterNotNull()
                .testIn(backgroundScope)

            val routine3DayTurbine = timeRoutineRepo
                .getTimeRoutineCompositionByDayOfWeek(routine3Days.first())
                .filterNotNull()
                .testIn(backgroundScope)

            backgroundScope.launch {
                timeRoutineRepo.addTimeRoutineComposition(routine1Composition)
                timeRoutineRepo.addTimeRoutineComposition(routine2Composition)
            }

            //addTimeRoutine 종료될때까지 대기.
            advanceUntilIdle()

            //routine2가 저장되어 수신이 되는가?
            val emitted = routine2DayTurbine.awaitItem()
            assert(emitted == routine2Composition)

            //other조건은 수신되면 안된다.
            routine3DayTurbine.expectNoEvents()

            // 남은 이벤트는 모두 무시.
            routine2DayTurbine.cancelAndIgnoreRemainingEvents()
            routine3DayTurbine.cancelAndIgnoreRemainingEvents()
        }
    }
}