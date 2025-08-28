package software.seriouschoi.timeisgold.data.repositories.timeslot

import app.cash.turbine.testIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
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
    fun deleteTimeSlot_should_DeletedTimeSlotAndMemo() {
        runTest {
            val timeSlotsTurbine = timeSlotRepo
                .getTimeSlotList(timeRoutine1Saved.timeRoutine.uuid)
                .testIn(backgroundScope)

            val slotForDelete = timeRoutine1Saved.timeSlots.first()
            val slotForNotDelete = timeRoutine1Saved.timeSlots.last()

            backgroundScope.launch {
                timeSlotRepo.deleteTimeSlot(slotForDelete.uuid)
            }

            advanceUntilIdle()

            val emitted = timeSlotsTurbine.awaitItem()
            val isExistSlotForDelete = emitted.any {
                it == slotForDelete
            }
            val isExistSlotForNotDelete = emitted.any {
                it == slotForNotDelete
            }
            assert(!isExistSlotForDelete)
            assert(isExistSlotForNotDelete)

            timeSlotsTurbine.cancelAndIgnoreRemainingEvents()
        }
    }
}