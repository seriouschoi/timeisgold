package software.seriouschoi.timeisgold.data.repositories.timeroutine

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import java.time.DayOfWeek
import java.util.UUID

@RunWith(AndroidJUnit4::class)
internal class AddTimeRoutineTest : BaseRoomTest() {

    /**
     * 타임루틴이 정상적으로 저장되는가?
     */
    @Test
    fun addTimeRoutine_whenQueriedOnCorrectDay_shouldReturnEntity() = runTest {
        val routineForAdd: TimeRoutineComposition = testFixtures.routineCompoMonTue
        val routineFlow = timeRoutineRepo
            .observeCompositionByUuidFlow(routineForAdd.timeRoutine.uuid)

        timeRoutineRepo.saveTimeRoutineComposition(routineForAdd)
        assert(routineForAdd == routineFlow.first())
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
        timeRoutineRepo.saveTimeRoutineComposition(routineCompo1)

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
        timeRoutineRepo.saveTimeRoutineComposition(routine2Compo)


        val testDayFlow = timeRoutineRepo
            .observeCompositionByDayOfWeek(testDay)
        assert(testDayFlow.first() == routine2Compo)
    }
}