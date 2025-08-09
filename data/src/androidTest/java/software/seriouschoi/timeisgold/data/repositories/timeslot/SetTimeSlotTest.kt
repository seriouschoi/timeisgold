package software.seriouschoi.timeisgold.data.repositories.timeslot

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import java.time.LocalTime

/**
 * Created by jhchoi on 2025. 8. 7.
 * jhchoi@neofect.com
 */
@RunWith(AndroidJUnit4::class)
internal class SetTimeSlotTest : BaseRoomTest() {
    @Before
    fun setup() {
        runTest {
            val dayOfWeeks = timeSlotTestFixtures.getTestScheduleDayOfWeeks1()
            val timeSchedule = timeSlotTestFixtures.createTimeSchedule(dayOfWeeks)
            timeScheduleRepo.addTimeSchedule(timeSchedule)

            timeSlotTestFixtures.createDetailDataList().forEach {
                timeSlotRepo.addTimeSlot(
                    timeSlotData = it,
                    timeScheduleUuid = timeSchedule.uuid
                )
            }
        }
    }

    @Test
    fun setTimeSlot_should_PersistEntityCorrectly() {
        runTest {
            val dayOfWeek = timeSlotTestFixtures.getTestScheduleDayOfWeeks1().first()
            val schedule = timeScheduleRepo.getTimeSchedule(dayOfWeek)
                ?: throw IllegalStateException("time schedule is null")

            val allTimeSlotList = schedule.timeSlotList.map {
                timeSlotRepo.getTimeSlotDetail(it.uuid)
            }
            val testData =
                allTimeSlotList.first() ?: throw IllegalStateException("test data is null")

            val changedTimeSlot = testData.timeSlotData.copy(
                title = "test_title_changed",
                startTime = LocalTime.now().minusMinutes(10),
            )
            val changedData = testData.copy(
                timeSlotData = changedTimeSlot
            )
            timeSlotRepo.setTimeSlot(changedData)

            val compareData = timeSlotRepo.getTimeSlotDetail(changedData.timeSlotData.uuid)
            assert(changedData == compareData)
        }
    }

    @Test
    fun setTimeSlot_withoutMemo_should_PersistEntityCorrectly() {
        runTest {
            val dayOfWeek = timeSlotTestFixtures.getTestScheduleDayOfWeeks1().first()
            val schedule = timeScheduleRepo.getTimeSchedule(dayOfWeek)
                ?: throw IllegalStateException("time schedule is null")

            val testData = timeSlotTestFixtures.createDetailTimeSlot().copy(
                timeSlotMemoData = null
            )
            timeSlotRepo.addTimeSlot(
                timeSlotData = testData,
                timeScheduleUuid = schedule.timeScheduleData.uuid
            )

            val changedTimeSlot = testData.timeSlotData.copy(
                title = "test_title_changed"
            )
            val changedData = testData.copy(
                timeSlotData = changedTimeSlot
            )
            timeSlotRepo.setTimeSlot(changedData)

            val compareData = timeSlotRepo.getTimeSlotDetail(changedData.timeSlotData.uuid)
            assert(changedData == compareData)
        }
    }

    @Test
    fun setTimeSlot_deleteMemo_should_PersistEntityCorrectly() {
        runTest {
            val dayOfWeek = timeSlotTestFixtures.getTestScheduleDayOfWeeks1().first()
            val schedule = timeScheduleRepo.getTimeSchedule(dayOfWeek)
                ?: throw IllegalStateException("time schedule is null")

            val allTimeSlotList = schedule.timeSlotList.map {
                timeSlotRepo.getTimeSlotDetail(it.uuid)
            }
            //데이터 변경.(메모 삭제)
            val testData =
                allTimeSlotList.first() ?: throw IllegalStateException("test data is null")
            val changedData = testData.copy(
                timeSlotMemoData = null
            )
            timeSlotRepo.setTimeSlot(changedData)

            //변경된 데이터가 잘 적용됐는가?
            val compareData = timeSlotRepo.getTimeSlotDetail(changedData.timeSlotData.uuid)
            assert(changedData == compareData)
            assert(changedData.timeSlotMemoData == null)
        }
    }

}