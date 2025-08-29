package software.seriouschoi.timeisgold.data.repositories.timeroutine

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.composition.TimeRoutineComposition
import java.time.DayOfWeek

@RunWith(AndroidJUnit4::class)
internal class AddTimeRoutineTest : BaseRoomTest() {

    /**
     * 타임루틴이 정상적으로 저장되는가?
     */
    @Test
    fun addTimeRoutine_whenQueriedOnCorrectDay_shouldReturnEntity() = runTest {
        val routineForAdd: TimeRoutineComposition = testFixtures.routineCompoMonTue
        val routineFlow = timeRoutineRepo
            .getTimeRoutineCompositionByUuid(routineForAdd.timeRoutine.uuid)

        timeRoutineRepo.addTimeRoutineComposition(routineForAdd)
        assert(routineForAdd == routineFlow.first())
    }


    /**
     * 중복된 uuid를 추가할때 예외가 발생하는가?
     */
    @Test(expected = Exception::class)
    fun addTimeRoutine_duplicateUuid_shouldThrowException() = runTest {
        val routine1 = testFixtures.routineCompoMonTue
        //routine1과 같은 uuid를 가진 routine 생성.
        val routine2 = testFixtures.routineCompoWedThu.let {
            val routine = it.timeRoutine.copy(
                uuid = routine1.timeRoutine.uuid
            )
            it.copy(
                timeRoutine = routine
            )
        }

        timeRoutineRepo.addTimeRoutineComposition(routine1)
        timeRoutineRepo.addTimeRoutineComposition(routine2)
    }

    /**
     * 같은 요일에 루틴을 추가하면, 갱신 되는가?
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addTimeRoutine_duplicateDayOfWeek_updateTimeRoutine() = runTest {
        val testDay = DayOfWeek.MONDAY
        val routineCompo1 = TimeRoutineComposition(
            timeRoutine = testFixtures.generateTimeRoutine(
                routineTitle = "test routine 1",
                createDayAgo = 2
            ),
            timeSlots = testFixtures.generateTimeSlotList(),
            dayOfWeeks = listOf(testDay, DayOfWeek.TUESDAY).map {
                it.toTimeRoutineDayOfWeekEntity()
            }.toSet()
        )
        timeRoutineRepo.addTimeRoutineComposition(routineCompo1)

        //같은 요일 루틴 추가.
        val routine2Compo = TimeRoutineComposition(
            timeRoutine = testFixtures.generateTimeRoutine(
                routineTitle = "test routine 2",
                createDayAgo = 1
            ),
            timeSlots = testFixtures.generateTimeSlotList(),
            dayOfWeeks = listOf(testDay).map {
                it.toTimeRoutineDayOfWeekEntity()
            }.toSet()
        )
        timeRoutineRepo.addTimeRoutineComposition(routine2Compo)


        val testDayFlow = timeRoutineRepo
            .getTimeRoutineCompositionByDayOfWeek(testDay)
        assert(testDayFlow.first() == routine2Compo)
    }


    /**
     * 중복된 타임 슬롯 id를 저장할 경우. Exception발생.
     */
    @Test(expected = Exception::class)
    fun addTimeSlot_duplicateUuid_shouldThrowException() = runTest {
        val routine1 = testFixtures.routineCompoMonTue
        val routine2 = testFixtures.routineCompoWedThu.copy(
            timeSlots = listOf(
                listOf(routine1.timeSlots.first()),
                testFixtures.routineCompoWedThu.timeSlots
            ).flatten()
        )

        timeRoutineRepo.addTimeRoutineComposition(routine1)
        timeRoutineRepo.addTimeRoutineComposition(routine2)
    }

}