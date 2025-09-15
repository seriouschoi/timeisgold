package software.seriouschoi.timeisgold.data.repositories.timeroutine

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import java.time.DayOfWeek
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
            val testDay = DayOfWeek.MONDAY
            val testDayRoutineFlow = timeRoutineRepo.observeTimeRoutineByDayOfWeek(testDay)
            val emitted = testDayRoutineFlow.first()
            assertNull(emitted)
        }
    }

    /**
     * 저장된 타임루틴이 정상적으로 불러와지는가?
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun givenRoutine_whenQuery_returnRoutine() = runTest {
        val mondayFlow = timeRoutineRepo.observeCompositionByDayOfWeek(DayOfWeek.MONDAY)

        val routineCompoMonTue = testFixtures.routineCompoMonTue
        timeRoutineRepo.saveTimeRoutineComposition(routineCompoMonTue)

        assert(mondayFlow.first() == routineCompoMonTue)
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
        val routineCompoMonTue = testFixtures.routineCompoMonTue
        val routineCompoWedThu = testFixtures.routineCompoWedThu.copy(
            dayOfWeeks = routineCompoMonTue.dayOfWeeks
        )

        val mondayRoutineFlow = timeRoutineRepo
            .observeCompositionByDayOfWeek(DayOfWeek.MONDAY)

        timeRoutineRepo.saveTimeRoutineComposition(routineCompoMonTue)
        assert(mondayRoutineFlow.first() == routineCompoMonTue)

        timeRoutineRepo.saveTimeRoutineComposition(routineCompoWedThu)
        assert(mondayRoutineFlow.first() == routineCompoWedThu)
    }

    /**
     * 요일로 타임루틴이 조회가 되는가?
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getTimeRoutineCompositionByDayOfWeek_returnsOnlyMatchingDay() = runTest {
        //TimeRoutine을 넣었을때, 정확한 요일로 조회가 되는가?
        val routineCompoMonTue = testFixtures.routineCompoMonTue
        val monTueDay = routineCompoMonTue.dayOfWeeks.map { it.dayOfWeek }
        val routineCompoWedThu = testFixtures.routineCompoWedThu
        val wedThuDay = routineCompoWedThu.dayOfWeeks.map { it.dayOfWeek }
        val routineCompoSun = testFixtures.routineCompoSun
        val sunDay = routineCompoSun.dayOfWeeks.map { it.dayOfWeek }

        val monRoutineFlow = timeRoutineRepo
            .observeCompositionByDayOfWeek(monTueDay.first())
        val wedRoutineFlow = timeRoutineRepo
            .observeCompositionByDayOfWeek(wedThuDay.first())
        val sundayRoutineFlow = timeRoutineRepo
            .observeCompositionByDayOfWeek(sunDay.first())

        timeRoutineRepo.saveTimeRoutineComposition(routineCompoMonTue)
        timeRoutineRepo.saveTimeRoutineComposition(routineCompoWedThu)

        //routine2가 저장되어 수신이 되는가?
        assert(monRoutineFlow.first() == routineCompoMonTue)
        assert(wedRoutineFlow.first() == routineCompoWedThu)
        assert(sundayRoutineFlow.first() == null)
    }
}