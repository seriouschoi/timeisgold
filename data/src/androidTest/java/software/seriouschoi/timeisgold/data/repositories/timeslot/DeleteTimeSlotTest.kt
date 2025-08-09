package software.seriouschoi.timeisgold.data.repositories.timeslot

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import software.seriouschoi.timeisgold.data.BaseRoomTest

/**
 * Created by jhchoi on 2025. 8. 7.
 * jhchoi@neofect.com
 */
internal class DeleteTimeSlotTest : BaseRoomTest() {

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
    fun deleteTimeSlot_should_DeletedTimeSlotAndMemo() {
        runTest {
            val dayOfWeek = timeSlotTestFixtures.getTestScheduleDayOfWeeks1().first()
            val schedule = timeScheduleRepo.getTimeScheduleDetail(dayOfWeek)
                ?: throw IllegalStateException("time schedule is null")

            val timeSlotDetailList = schedule.timeSlotList.map {
                timeSlotRepo.getTimeSlotDetail(it.uuid)
            }

            //삭제.
            val timeSlot =
                timeSlotDetailList.first() ?: throw IllegalStateException("test data is null")
            timeSlotRepo.deleteTimeSlot(timeSlot.timeSlotData.uuid)

            //삭제된 정보를 조회.
            val compareData = timeSlotRepo.getTimeSlotDetail(timeSlot.timeSlotData.uuid)
            assert(compareData == null)

            //다른 데이터는 잘 있는가?
            val otherTimeSlot =
                timeSlotDetailList[1] ?: throw IllegalStateException("test data is null")
            val compareOtherData = timeSlotRepo.getTimeSlotDetail(otherTimeSlot.timeSlotData.uuid)
            assert(compareOtherData == otherTimeSlot)
        }
    }
}