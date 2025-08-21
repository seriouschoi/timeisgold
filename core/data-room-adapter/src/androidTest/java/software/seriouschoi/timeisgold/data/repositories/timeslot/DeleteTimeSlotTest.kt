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
    fun deleteTimeSlot_should_DeletedTimeSlotAndMemo() {
        runTest {
            val dayOfWeek = timeSlotTestFixtures.getTestRoutineDayOfWeeks1().first()
            val routine = timeRoutineRepo.getTimeRoutineDetail(dayOfWeek)
                ?: throw IllegalStateException("time routine is null")

            val timeSlotDetailList = routine.timeSlotList.map {
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