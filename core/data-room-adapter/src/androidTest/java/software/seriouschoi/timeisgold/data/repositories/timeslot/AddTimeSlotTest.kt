package software.seriouschoi.timeisgold.data.repositories.timeslot

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import java.time.DayOfWeek

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
        val slotListFlow = timeSlotRepo.observeTimeSlotList(
            timeRoutineAdded.timeRoutine.uuid
        )

        val newTimeSlots =
            testFixtures.generateTimeSlotList(startHour = 12, endHour = 13)

        newTimeSlots.forEach {
            timeSlotRepo.addTimeSlot(
                TimeSlotComposition(it),
                timeRoutineAdded.timeRoutine.uuid
            )
        }

        val emitted = slotListFlow.first()
        val isAddedNewSlots = emitted.all { it: TimeSlotEntity ->
            newTimeSlots.contains(
                it
            )
        }
        assert(isAddedNewSlots)

    }

    /*
    다른 로직에 의해 오류가 나는건데 의도된 오류로 착각하면 테스트 의미가 없음.
     */
    @Test(expected = SQLiteConstraintException::class)
    fun addTimeSlot_duplicateUuid_shouldThrowException() {
        runTest {
            //test data 준비.
            val routine1 = TimeRoutineComposition(
                timeRoutine = testFixtures.routineCompoMonTue.timeRoutine,
                timeSlots = testFixtures.generateTimeSlotList(startHour = 12, endHour = 13),
                dayOfWeeks = listOf(DayOfWeek.SATURDAY).map {
                    it.toTimeRoutineDayOfWeekEntity()
                }.toSet()
            )
            timeRoutineRepo.addTimeRoutineComposition(routine1)

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