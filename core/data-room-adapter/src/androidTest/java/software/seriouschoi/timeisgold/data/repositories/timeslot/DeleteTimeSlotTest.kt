package software.seriouschoi.timeisgold.data.repositories.timeslot

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import software.seriouschoi.timeisgold.data.BaseRoomTest

/**
 * Created by jhchoi on 2025. 8. 7.
 * jhchoi
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class DeleteTimeSlotTest : BaseRoomTest() {

    private val timeRoutine1Saved = testFixtures.routineCompoMonTue

    @Before
    fun setup() {
        runTest {
            timeRoutineRepo.addTimeRoutineComposition(timeRoutine1Saved)
        }
    }

    @Test
    fun deleteTimeSlot_should_DeletedTimeSlotAndMemo() = runTest {
        val timeSlotsFlow = timeSlotRepo
            .observeTimeSlotList(timeRoutine1Saved.timeRoutine.uuid)

        val slotForDelete = timeRoutine1Saved.timeSlots.first()
        val slotForNotDelete = timeRoutine1Saved.timeSlots.last()

        timeSlotRepo.deleteTimeSlot(slotForDelete.uuid)


        val emitted = timeSlotsFlow.first()
        val isExistSlotForDelete = emitted.any {
            it == slotForDelete
        }
        val isExistSlotForNotDelete = emitted.any {
            it == slotForNotDelete
        }
        assert(!isExistSlotForDelete)
        assert(isExistSlotForNotDelete)
    }
}