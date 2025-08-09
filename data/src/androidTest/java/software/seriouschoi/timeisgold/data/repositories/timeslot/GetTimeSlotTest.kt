package software.seriouschoi.timeisgold.data.repositories.timeslot

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest

/**
 * Created by jhchoi on 2025. 8. 7.
 * jhchoi@neofect.com
 */
@RunWith(AndroidJUnit4::class)
internal class GetTimeSlotTest : BaseRoomTest() {

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
    fun getTimeSlotList_should_ReturnTimeSlotList() {
        runTest {
            val dayOfWeek = timeSlotTestFixtures.getTestScheduleDayOfWeeks1().first()
            val schedule = timeScheduleRepo.getTimeSchedule(dayOfWeek)
                ?: throw IllegalStateException("time schedule is null")

            //테스트를 위한 목록 추가.
            val timeSlotDetailList = timeSlotTestFixtures.createDetailDataList()
            timeSlotDetailList.forEach {
                timeSlotRepo.addTimeSlot(it, schedule.timeScheduleData.uuid)
            }

            val timeSlotList = timeSlotDetailList.map {
                it.timeSlotData
            }.sortedBy {
                it.uuid
            }

            val addTimeSlotAfterSchedule = timeScheduleRepo.getTimeScheduleByUuid(schedule.timeScheduleData.uuid)
                ?: throw IllegalStateException("test data is null")

            //가져온 데이터에 새로 추가된 데이터가 있는가?

            val compareList = addTimeSlotAfterSchedule.timeSlotList.filter {
                timeSlotList.contains(it)
            }.sortedBy {
                it.uuid
            }

            assert(timeSlotList == compareList)
        }
    }

    @Test
    fun getTimeSlot_should_ReturnTimeSlot() {
        runTest {
            val dayOfWeek = timeSlotTestFixtures.getTestScheduleDayOfWeeks1().first()
            val schedule = timeScheduleRepo.getTimeSchedule(dayOfWeek)
                ?: throw IllegalStateException("time schedule is null")

            val testData = timeSlotTestFixtures.createDetailTimeSlot()
            timeSlotRepo.addTimeSlot(
                timeSlotData = testData,
                timeScheduleUuid = schedule.timeScheduleData.uuid
            )

            val compareData = timeSlotRepo.getTimeSlotDetail(testData.timeSlotData.uuid)
            assert(compareData == testData)
        }
    }

    @Test
    fun getTimeSlot_withDeletedTimeSlot_should_ReturnNull() {
        runTest {
            //없는 데이터상태를 만들기 위해 기존 데이터중 하나를 삭제.
            val dayOfWeek = timeSlotTestFixtures.getTestScheduleDayOfWeeks1().first()
            val schedule = timeScheduleRepo.getTimeSchedule(dayOfWeek)
                ?: throw IllegalStateException("time schedule is null")
            val testData = schedule.timeSlotList.first().let {
                timeSlotRepo.getTimeSlotDetail(it.uuid)
            } ?: throw IllegalStateException("test data is null")
            timeSlotRepo.deleteTimeSlot(testData.timeSlotData.uuid)

            //없는 데이터를 요청하면 null로 리턴하는가?
            val compareData = timeSlotRepo.getTimeSlotDetail(testData.timeSlotData.uuid)
            assert(compareData == null)
        }
    }

    @Test
    fun getTimeSlot_withDeletedTimeSlotMemo_should_ReturnOnlyTimeSlot() {
        runTest {
            val dayOfWeek = timeSlotTestFixtures.getTestScheduleDayOfWeeks1().first()
            val schedule = timeScheduleRepo.getTimeSchedule(dayOfWeek)
                ?: throw IllegalStateException("time schedule is null")
            //메모가 없는 데이터를 저장.
            val testData = schedule.timeSlotList.first().let {
                timeSlotRepo.getTimeSlotDetail(it.uuid)?.copy(
                    timeSlotMemoData = null
                )
            } ?: throw IllegalStateException("test data is null")
            timeSlotRepo.setTimeSlot(testData)


            val compareData = timeSlotRepo.getTimeSlotDetail(testData.timeSlotData.uuid)
                ?: throw IllegalStateException("test data is null")
            assert(compareData.timeSlotData == testData.timeSlotData)
            assert(compareData.timeSlotMemoData == null)
        }
    }
}