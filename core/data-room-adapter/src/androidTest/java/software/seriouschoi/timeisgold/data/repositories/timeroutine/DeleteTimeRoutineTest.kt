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
            timeRoutineRepo.addTimeRoutineComposition(routine1)
            timeRoutineRepo.addTimeRoutineComposition(routine2)
        }
    }

    @Test
    fun deleteTimeRoutine_should_allRelationData() = runTest {

        //루틴 1 삭제.
        timeRoutineRepo.deleteTimeRoutine(timeRoutineUuid = routine1.timeRoutine.uuid)

        //루틴 1 삭제 확인.
        val emittedRoutine1 =
            timeRoutineRepo.getTimeRoutineCompositionByUuid(routine1.timeRoutine.uuid).first()
        assert(emittedRoutine1 == null) { "routine1 is not deleted" }

        val emittedRoutine2 =
            timeRoutineRepo.getTimeRoutineCompositionByUuid(routine2.timeRoutine.uuid).first()
        assert(emittedRoutine2 == routine2) { "routine2 is deleted" }

        //루틴 2 유지 확인.
        val emittedRoutine1TimeSlots =
            timeSlotRepo.getTimeSlotList(routine1.timeRoutine.uuid).first()
        assert(emittedRoutine1TimeSlots.isEmpty()) { "routine1 time slot is not deleted" }

        val emittedRoutine2TimeSlots =
            timeSlotRepo.getTimeSlotList(routine2.timeRoutine.uuid).first()
        assert(emittedRoutine2TimeSlots == routine2.timeSlots) { "routine2 time slot is deleted" }
    }
}