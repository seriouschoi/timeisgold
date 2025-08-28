package software.seriouschoi.timeisgold.data.repositories.timeroutine

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.testIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest

@RunWith(AndroidJUnit4::class)
internal class DeleteTimeRoutineTest : BaseRoomTest() {
    private val routine1 = testFixtures.routineCompoMonTue
    private val routine2 = testFixtures.routineCompoWedThu

    @Before
    fun setup() {
        runTest {
            timeRoutineRepo.addTimeRoutineComposition(routine1)
            timeRoutineRepo.addTimeRoutineComposition(routine2)
        }
    }

    /**
     * time routine을 삭제했을때, 관련된 모든 엔티티가 같이 삭제되었는가?
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteTimeRoutine_should_allRelationData() {
        runTest {

            //루틴 1 수집.
            val routine1Turbine =
                timeRoutineRepo.getTimeRoutineCompositionByUuid(routine1.timeRoutine.uuid).testIn(backgroundScope)
            val routine1TimeSlotTurbine =
                timeSlotRepo.getTimeSlotList(routine1.timeRoutine.uuid).testIn(backgroundScope)

            //루틴 2 수집
            val routine2Turbine =
                timeRoutineRepo.getTimeRoutineCompositionByUuid(routine2.timeRoutine.uuid).testIn(backgroundScope)
            val routine2TimeSlotTurbine =
                timeSlotRepo.getTimeSlotList(routine2.timeRoutine.uuid).testIn(backgroundScope)

            //루틴 1 삭제.
            backgroundScope.launch {
                timeRoutineRepo.deleteTimeRoutine(timeRoutineUuid = routine1.timeRoutine.uuid)
            }

            advanceUntilIdle()

            //루틴 1 삭제 확인.
            val emittedRoutine1 = routine1Turbine.awaitItem()
            assert(emittedRoutine1 == null)

            val emittedRoutine2 = routine2Turbine.awaitItem()
            assert(emittedRoutine2 == routine2)

            //루틴 2 유지 확인.
            val emittedRoutine1TimeSlots = routine1TimeSlotTurbine.awaitItem()
            assert(emittedRoutine1TimeSlots.isEmpty())

            val emittedRoutine2TimeSlots = routine2TimeSlotTurbine.awaitItem()
            assert(emittedRoutine2TimeSlots == routine2.timeSlots)

            //수집 종료.
            routine1Turbine.cancelAndIgnoreRemainingEvents()
            routine1TimeSlotTurbine.cancelAndIgnoreRemainingEvents()
        }
    }
}