package software.seriouschoi.timeisgold.data.repositories.timeroutine

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest

@RunWith(AndroidJUnit4::class)
internal class DeleteTimeRoutineTest : BaseRoomTest() {

    @Before
    fun setup() {
        runTest {
            val dayOfWeeks1 = timeSlotTestFixtures.getTestRoutineDayOfWeeks1()
            val routine1 = timeSlotTestFixtures.createTimeRoutine(dayOfWeeks1)
            timeRoutineRepo.addTimeRoutine(routine1)

            timeSlotTestFixtures.createDetailDataList().forEach {
                timeSlotRepo.addTimeSlot(
                    timeSlotData = it,
                    timeRoutineUuid = routine1.timeRoutine.uuid
                )
            }

            val dayOfWeeks2 = timeSlotTestFixtures.getTestRoutineDayOfWeeks2()
            val routine2 = timeSlotTestFixtures.createTimeRoutine(dayOfWeeks2)
            timeRoutineRepo.addTimeRoutine(routine2)

            timeSlotTestFixtures.createDetailDataList().forEach {
                timeSlotRepo.addTimeSlot(
                    timeSlotData = it,
                    timeRoutineUuid = routine2.timeRoutine.uuid
                )
            }
        }
    }

    @Test
    fun deleteTimeRoutine_should_allRelationData() {
        //time routine을 삭제했을때, 관련된 모든 엔티티가 같이 삭제되었는가?
        runTest {
            val dayOfWeek = timeSlotTestFixtures.getTestRoutineDayOfWeeks1().first()
            val routine = timeRoutineRepo.getTimeRoutine(dayOfWeek).first()
                ?: throw IllegalStateException("time routine is null")
            val timeSlotList =timeSlotRepo.getTimeSlotList(routine.uuid)

            timeRoutineRepo.deleteTimeRoutine(routine.uuid)

            val compareData = timeRoutineRepo.getTimeRoutineDetailByUuid(routine.uuid)

            assert(compareData == null)
            timeSlotList.forEach { timeSlot ->
                val compareTimeSlot = timeSlotRepo.getTimeSlotDetail(timeSlot.uuid)
                assert(compareTimeSlot == null)
            }
        }
    }

    @Test
    fun deleteTimeRoutine_byOneDayOfWeek_shouldReturnData() {
        //요일중 하나를 지워도 정상 동작 하는가?
        runTest {
            val dayOfWeek1 = timeSlotTestFixtures.getTestRoutineDayOfWeeks1().first()
            val routine1 = timeRoutineRepo.getTimeRoutine(dayOfWeek1).first()
                ?: throw IllegalStateException("time routine is null")


            val dayOfWeekForDelete = timeSlotTestFixtures.getTestRoutineDayOfWeeks2().first()
            val routineForDelete =
                timeRoutineRepo.getTimeRoutine(dayOfWeekForDelete).first() ?: throw IllegalStateException(
                    "time routine is null"
                )
            timeRoutineRepo.deleteTimeRoutine(routineForDelete.uuid)

            val compareData1 =
                timeRoutineRepo.getTimeRoutineDetailByUuid(routine1.uuid)
            assert(compareData1 == routine1)
        }
    }

    @Test
    fun deleteDayOfWeek_shouldReturnNull() {
        //요일만 삭제하는 로직은 도메인에 없는 동작. 현재 요일은 routine update로 처리됨.
    }
}