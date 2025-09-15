package software.seriouschoi.timeisgold.data.repositories.timeslot

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
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
            timeRoutineRepo.saveTimeRoutineComposition(routineForAdd1)
        }
    }

    @Test
    fun setTimeSlot_should_PersistEntityCorrectly() = runTest {
        val slotFotUpdate = routineForAdd1.timeSlots.first().copy(
            title = "test_title_changed",
            startTime = LocalTime.now().minusMinutes(10),
        )

        val slotDetailFlow = timeSlotRepo
            .getTimeSlotDetail(slotFotUpdate.uuid)

        timeSlotRepo.setTimeSlot(
            timeSlotData = TimeSlotComposition(
                slotFotUpdate
            )
        )

        val emitted = slotDetailFlow.first()
        assert(emitted?.timeSlotData == slotFotUpdate)
    }
}