package software.seriouschoi.timeisgold.data.repositories.timeschedule

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest

@RunWith(AndroidJUnit4::class)
internal class DeleteTimeScheduleTest : BaseRoomTest() {
    @Before
    fun setup() {
        runTest {
            val dayOfWeeks1 = timeSlotTestFixtures.getTestScheduleDayOfWeeks1()
            val schedule1 = timeSlotTestFixtures.createTimeSchedule(dayOfWeeks1)
            timeScheduleRepo.addTimeSchedule(schedule1)

            timeSlotTestFixtures.createDetailDataList().forEach {
                timeSlotRepo.addTimeSlot(
                    timeSlotData = it,
                    timeScheduleUuid = schedule1.uuid
                )
            }

            val dayOfWeeks2 = timeSlotTestFixtures.getTestScheduleDayOfWeeks2()
            val schedule2 = timeSlotTestFixtures.createTimeSchedule(dayOfWeeks2)
            timeScheduleRepo.addTimeSchedule(schedule2)

            timeSlotTestFixtures.createDetailDataList().forEach {
                timeSlotRepo.addTimeSlot(
                    timeSlotData = it,
                    timeScheduleUuid = schedule2.uuid
                )
            }
        }
    }

    @Test
    fun deleteTimeSchedule_should_allRelationData() {
        //time schedule을 삭제했을때, 관련된 모든 엔티티가 같이 삭제되었는가?
        runTest {
            val dayOfWeek = timeSlotTestFixtures.getTestScheduleDayOfWeeks1().first()
            val schedule = timeScheduleRepo.getTimeSchedule(dayOfWeek)
                ?: throw IllegalStateException("time schedule is null")

            timeScheduleRepo.deleteTimeSchedule(schedule.timeScheduleData.uuid)

            val compareData = timeScheduleRepo.getTimeScheduleByUuid(schedule.timeScheduleData.uuid)

            assert(compareData == null)
            schedule.timeSlotList.forEach { timeSlot ->
                val compareTimeSlot = timeSlotRepo.getTimeSlotDetail(timeSlot.uuid)
                assert(compareTimeSlot == null)
            }
        }
    }

    @Test
    fun deleteTimeSchedule_whenWrongDay_shouldReturnData() {
        //엉뚱한 요일을 지워도 정상 동작 하는가?
        runTest {
            val dayOfWeek1 = timeSlotTestFixtures.getTestScheduleDayOfWeeks1().first()
            val schedule1 = timeScheduleRepo.getTimeSchedule(dayOfWeek1)
                ?: throw IllegalStateException("time schedule is null")

            val dayOfWeekForDelete = timeSlotTestFixtures.getTestScheduleDayOfWeeks2().first()
            val scheduleForDelete =
                timeScheduleRepo.getTimeSchedule(dayOfWeekForDelete) ?: throw IllegalStateException(
                    "time schedule is null"
                )

            timeScheduleRepo.deleteTimeSchedule(scheduleForDelete.timeScheduleData.uuid)

            val compareData1 =
                timeScheduleRepo.getTimeScheduleByUuid(schedule1.timeScheduleData.uuid)
            assert(compareData1 == schedule1)
        }
    }

    @Test
    fun deleteDayOfWeek_shouldReturnNull() {
        //요일만 삭제하는 로직은 도메인에 없는 동작. 현재 요일은 schedule update로 처리됨.
    }
}