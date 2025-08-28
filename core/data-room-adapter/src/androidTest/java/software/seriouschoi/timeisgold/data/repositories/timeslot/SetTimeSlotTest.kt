package software.seriouschoi.timeisgold.data.repositories.timeslot

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
import software.seriouschoi.timeisgold.domain.composition.TimeSlotComposition
import java.time.LocalTime

/**
 * Created by jhchoi on 2025. 8. 7.
 * jhchoi
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class SetTimeSlotTest : BaseRoomTest() {
    val routineForAdd1 = testFixtures.routineCompoMonTue

    @Before
    fun setup() {
        runTest {
            timeRoutineRepo.addTimeRoutineComposition(routineForAdd1)
        }
    }

    @Test
    fun setTimeSlot_should_PersistEntityCorrectly() {
        runTest {
            val slotFotUpdate = routineForAdd1.timeSlots.first().copy(
                title = "test_title_changed",
                startTime = LocalTime.now().minusMinutes(10),
            )

            val turbine = timeSlotRepo
                .getTimeSlotDetail(slotFotUpdate.uuid)
                .testIn(backgroundScope)

            backgroundScope.launch {
                timeSlotRepo.setTimeSlot(
                    timeSlotData = TimeSlotComposition(
                        slotFotUpdate
                    )
                )
            }

            advanceUntilIdle()

            val emitted = turbine.awaitItem()
            assert(emitted?.timeSlotData == slotFotUpdate)

            turbine.cancelAndIgnoreRemainingEvents()
        }
    }
}