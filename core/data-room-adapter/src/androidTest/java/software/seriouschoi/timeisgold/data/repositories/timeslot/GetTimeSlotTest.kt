package software.seriouschoi.timeisgold.data.repositories.timeslot

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.testIn
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import java.util.UUID
import kotlin.test.assertNull

/**
 * Created by jhchoi on 2025. 8. 7.
 * jhchoi
 */
@RunWith(AndroidJUnit4::class)
internal class GetTimeSlotTest : BaseRoomTest() {

    val routine = testFixtures.routineCompoMonTue

    @Before
    fun setup() {
        runTest {
            timeRoutineRepo.addTimeRoutineComposition(routine)
        }
    }

    /**
     * timeslot list를 요청하면, 저장된 timeslot list를 리턴하는가?
     */
    @Test
    fun getTimeSlotList_should_ReturnTimeSlotList() {
        runTest {
            val timeSlotTurbine =
                timeSlotRepo.getTimeSlotList(routine.timeRoutine.uuid).testIn(backgroundScope)

            val timeSlotList = timeSlotTurbine.awaitItem()
            assert(timeSlotList == routine.timeSlots)

            timeSlotTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * timeslot을 요청하면, 지정된 timeslot을 리턴하는가?
     */
    @Test
    fun getTimeSlot_should_ReturnTimeSlot() {
        runTest {
            val timeSlot = routine.timeSlots.first()
            val timeSlotTurbine = timeSlotRepo.getTimeSlotDetail(timeSlot.uuid).testIn(
                backgroundScope
            )

            val emitted = timeSlotTurbine.awaitItem()
            assert(timeSlot == emitted)

            timeSlotTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * 없은 timeslot을 요청하면, null을 리턴하는가?
     */
    @Test
    fun getTimeSlot_withDeletedTimeSlot_should_ReturnNull() {
        runTest {
            //없는 데이터상태 요청.
            val turbine = timeSlotRepo.getTimeSlotDetail(UUID.randomUUID().toString()).testIn(
                backgroundScope
            )

            assertNull(turbine.awaitItem())
            turbine.cancelAndIgnoreRemainingEvents()
        }
    }
}