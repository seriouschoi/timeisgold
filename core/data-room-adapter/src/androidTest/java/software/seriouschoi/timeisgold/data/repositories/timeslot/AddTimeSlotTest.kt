package software.seriouschoi.timeisgold.data.repositories.timeslot

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import software.seriouschoi.timeisgold.domain.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.entities.TimeSlotEntity

@RunWith(AndroidJUnit4::class)
internal class AddTimeSlotTest : BaseRoomTest() {
    private val timeRoutineAdded = testFixtures.routineCompoMonTue.copy(
        timeSlots = emptyList()
    )

    @Before
    fun setup() {
        runTest {
            timeRoutineRepo.addTimeRoutineComposition(timeRoutineAdded)
        }
    }

    /**
     * 새로 추가된 타임 슬롯이 지정된 타임 루틴에 잘 저장되는가?
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addTimeSlot_should_PersistEntityCorrectly() = runTest {
        turbineScope {
            val timeSlotTurbine = timeSlotRepo.getTimeSlotList(
                timeRoutineAdded.timeRoutine.uuid
            ).testIn(backgroundScope)

            val newTimeSlots =
                testFixtures.generateTimeSlotList(startHour = 12, endHour = 13)

            backgroundScope.launch {
                newTimeSlots.forEach {
                    timeSlotRepo.addTimeSlot(
                        TimeSlotComposition(it),
                        timeRoutineAdded.timeRoutine.uuid
                    )
                }
            }
            advanceUntilIdle()

            val isAddedNewSlots = timeSlotTurbine.awaitItem().all { it: TimeSlotEntity ->
                newTimeSlots.contains(
                    it
                )
            }
            assert(isAddedNewSlots)

            timeSlotTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun addTimeSlot_duplicateUuid_shouldThrowException() {
        runTest {
            val routine1 = timeRoutineAdded
            val duplicatedSlot = routine1.timeSlots.first().copy(
                title = "new slot",
                createTime = System.currentTimeMillis()
            )

            timeSlotRepo.addTimeSlot(
                timeSlotData = TimeSlotComposition(timeSlotData = duplicatedSlot),
                timeRoutineUuid = routine1.timeRoutine.uuid
            )
        }
    }
}