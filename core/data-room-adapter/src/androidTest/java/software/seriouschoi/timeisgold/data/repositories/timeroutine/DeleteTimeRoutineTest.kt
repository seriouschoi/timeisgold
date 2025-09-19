package software.seriouschoi.timeisgold.data.repositories.timeroutine

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
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
            timeRoutineRepo.saveTimeRoutineComposition(routine1)
            timeRoutineRepo.saveTimeRoutineComposition(routine2)

        }
    }

    @Test
    fun deleteTimeRoutine_should_allRelationData() = runTest {
        //루틴 1 삭제.
        timeRoutineRepo.deleteTimeRoutine(timeRoutineUuid = routine1.timeRoutine.uuid)

        val routine1Flow = timeRoutineRepo.observeCompositionByUuidFlow(routine1.timeRoutine.uuid)
        val routine2Flow = timeRoutineRepo.observeCompositionByUuidFlow(routine2.timeRoutine.uuid)

        val routine1SlotFlow = timeSlotRepo.watchTimeSlotList(routine1.timeRoutine.uuid)
        val routine2SlotFlow = timeSlotRepo.watchTimeSlotList(routine2.timeRoutine.uuid)

        //루틴 1 삭제 확인.
        assert(routine1Flow.first() == null) { "routine1 is not deleted" }
        assert(routine1SlotFlow.first().isEmpty()) { "routine1 time slot is not deleted" }

        //루틴 2 유지 확인.
        assert(routine2Flow.first() == routine2) { "routine2 is deleted" }
        assert(routine2SlotFlow.first() == routine2.timeSlots) { "routine2 time slot is deleted" }
    }
}