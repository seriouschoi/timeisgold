package software.seriouschoi.timeisgold.data.repositories.timeslot

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest

@RunWith(AndroidJUnit4::class)
internal class AddTimeSlotTest : BaseRoomTest() {
    @Before
    fun setup() {
        runTest {
            val dayOfWeeks = timeSlotTestFixtures.getTestRoutineDayOfWeeks1()
            val timeRoutine = timeSlotTestFixtures.createTimeRoutine(dayOfWeeks)
            timeRoutineRepo.addTimeRoutine(timeRoutine)

            timeSlotTestFixtures.createDetailDataList().forEach {
                timeSlotRepo.addTimeSlot(
                    timeSlotData = it,
                    timeRoutineUuid = timeRoutine.uuid
                )
            }
        }
    }

    @Test
    fun addTimeSlot_should_PersistEntityCorrectly() {
        runTest {
            val testDayOfWeek = timeSlotTestFixtures.getTestRoutineDayOfWeeks1().first()
            val routine = timeRoutineRepo.getTimeRoutineDetail(testDayOfWeek)
                ?: throw IllegalStateException("time routine is null")

            val testTimeSlotDetail = timeSlotTestFixtures.createDetailDataList().first()
            timeSlotRepo.addTimeSlot(
                timeSlotData = testTimeSlotDetail,
                timeRoutineUuid = routine.timeRoutineData.uuid
            )

            val compareData =
                timeSlotRepo.getTimeSlotDetail(timeslotUuid = testTimeSlotDetail.timeSlotData.uuid)
                    ?: throw IllegalStateException("compare data is null. add time slot failed.")

            assert(compareData == testTimeSlotDetail)
        }
    }

    @Test
    fun addTimeSlot_withoutMemo_should_PersistEntityCorrectly() {
        runTest {
            val testDayOfWeek = timeSlotTestFixtures.getTestRoutineDayOfWeeks1().first()
            val routine = timeRoutineRepo.getTimeRoutineDetail(testDayOfWeek)
                ?: throw IllegalStateException("time routine is null")

            val testData = timeSlotTestFixtures.createDetailTimeSlot().copy(
                timeSlotMemoData = null
            )
            timeSlotRepo.addTimeSlot(
                timeSlotData = testData,
                timeRoutineUuid = routine.timeRoutineData.uuid
            )

            val compareData = timeSlotRepo.getTimeSlotDetail(testData.timeSlotData.uuid)
                ?: throw IllegalStateException("compare data is null. add time slot failed.")

            assert(compareData == testData)
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun addTimeSlot_duplicateUuid_shouldThrowException() {
        runTest {
            val testDayOfWeek = timeSlotTestFixtures.getTestRoutineDayOfWeeks1().first()
            val routine = timeRoutineRepo.getTimeRoutineDetail(testDayOfWeek)
                ?: throw IllegalStateException("time routine is null")

            val testData = timeSlotTestFixtures.createDetailTimeSlot()
            timeSlotRepo.addTimeSlot(testData, routine.timeRoutineData.uuid)

            //같은 uuid 의 다른 타임 슬롯 생성.
            val newTestData = timeSlotTestFixtures.createDetailTimeSlot()
            val newTestTimeSlot = newTestData.timeSlotData.copy(
                uuid = testData.timeSlotData.uuid
            )
            val testData2 = newTestData.copy(timeSlotData = newTestTimeSlot)
            timeSlotRepo.addTimeSlot(
                timeSlotData = testData2,
                timeRoutineUuid = routine.timeRoutineData.uuid
            )
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun addTimeSlot_duplicateMemoUuid_shouldThrowException() {
        runTest {
            val testDayOfWeek = timeSlotTestFixtures.getTestRoutineDayOfWeeks1().first()
            val routine = timeRoutineRepo.getTimeRoutineDetail(testDayOfWeek)
                ?: throw IllegalStateException("time routine is null")

            val testData1Source = timeSlotTestFixtures.createDetailTimeSlot()
            val testData1Memo =
                testData1Source.timeSlotMemoData ?: throw IllegalStateException("test data is null")
            timeSlotRepo.addTimeSlot(
                timeSlotData = testData1Source,
                timeRoutineUuid = routine.timeRoutineData.uuid
            )

            val testData2Source = timeSlotTestFixtures.createDetailTimeSlot()
            val testData2Memo = testData2Source.timeSlotMemoData?.copy(
                uuid = testData1Memo.uuid
            ) ?: throw IllegalStateException("test data is null")

            timeSlotRepo.addTimeSlot(
                timeSlotData = testData2Source.copy(
                    timeSlotMemoData = testData2Memo
                ),
                timeRoutineUuid = routine.timeRoutineData.uuid
            )
        }
    }
}