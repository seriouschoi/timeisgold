package software.seriouschoi.timeisgold.data.repositories.timeslot

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
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

    private val routine = testFixtures.routineCompoMonTue
    private val gson = Gson()

    @Before
    fun setup() {
        runTest {
            timeRoutineRepo.saveTimeRoutineComposition(routine)
        }
    }

    /**
     * timeslot list를 요청하면, 저장된 timeslot list를 리턴하는가?
     */
    @Test
    fun getTimeSlotList_should_ReturnTimeSlotList() = runTest {
        val slotListFlow =
            timeSlotRepo.observeTimeSlotList(routine.timeRoutine.uuid)

        val emitted = slotListFlow.first()
        assert(emitted == routine.timeSlots)
    }

    /**
     * timeslot을 요청하면, 지정된 timeslot을 리턴하는가?
     */
    @Test
    fun getTimeSlot_should_ReturnTimeSlot() = runTest {
        val timeSlot = routine.timeSlots.first()
        val slotFlow = timeSlotRepo.getTimeSlotDetail(timeSlot.uuid)

        val emitted = slotFlow.first()
        assert(timeSlot == emitted?.timeSlotData) {
            """
                    get timeslot failed.
                    emitted: ${gson.toJson(emitted)}
                    source: ${gson.toJson(timeSlot)}
                """.trimIndent()
        }

    }

    /**
     * 없은 timeslot을 요청하면, null을 리턴하는가?
     */
    @Test
    fun getTimeSlot_withDeletedTimeSlot_should_ReturnNull() = runTest {
        //없는 데이터상태 요청.
        val slotFlow = timeSlotRepo.getTimeSlotDetail(UUID.randomUUID().toString())

        assertNull(slotFlow.first())
    }
}