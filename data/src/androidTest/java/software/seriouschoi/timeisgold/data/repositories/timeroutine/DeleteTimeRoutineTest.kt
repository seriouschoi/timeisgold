package software.seriouschoi.timeisgold.data.repositories.timeroutine

import androidx.test.ext.junit.runners.AndroidJUnit4
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
                    timeRoutineUuid = routine1.uuid
                )
            }

            val dayOfWeeks2 = timeSlotTestFixtures.getTestRoutineDayOfWeeks2()
            val routine2 = timeSlotTestFixtures.createTimeRoutine(dayOfWeeks2)
            timeRoutineRepo.addTimeRoutine(routine2)

            timeSlotTestFixtures.createDetailDataList().forEach {
                timeSlotRepo.addTimeSlot(
                    timeSlotData = it,
                    timeRoutineUuid = routine2.uuid
                )
            }
        }
    }

    @Test
    fun deleteTimeRoutine_should_allRelationData() {
        //time routine을 삭제했을때, 관련된 모든 엔티티가 같이 삭제되었는가?
        runTest {
            val dayOfWeek = timeSlotTestFixtures.getTestRoutineDayOfWeeks1().first()
            val routine = timeRoutineRepo.getTimeRoutineDetail(dayOfWeek)
                ?: throw IllegalStateException("time routine is null")

            timeRoutineRepo.deleteTimeRoutine(routine.timeRoutineData.uuid)

            val compareData = timeRoutineRepo.getTimeRoutineDetailByUuid(routine.timeRoutineData.uuid)

            assert(compareData == null)
            routine.timeSlotList.forEach { timeSlot ->
                val compareTimeSlot = timeSlotRepo.getTimeSlotDetail(timeSlot.uuid)
                assert(compareTimeSlot == null)
            }
        }
    }

    @Test
    fun deleteTimeRoutine_whenWrongDay_shouldReturnData() {
        //엉뚱한 요일을 지워도 정상 동작 하는가?
        runTest {
            val dayOfWeek1 = timeSlotTestFixtures.getTestRoutineDayOfWeeks1().first()
            val routine1 = timeRoutineRepo.getTimeRoutineDetail(dayOfWeek1)
                ?: throw IllegalStateException("time routine is null")

            val dayOfWeekForDelete = timeSlotTestFixtures.getTestRoutineDayOfWeeks2().first()
            val routineForDelete =
                timeRoutineRepo.getTimeRoutineDetail(dayOfWeekForDelete) ?: throw IllegalStateException(
                    "time routine is null"
                )

            timeRoutineRepo.deleteTimeRoutine(routineForDelete.timeRoutineData.uuid)

            val compareData1 =
                timeRoutineRepo.getTimeRoutineDetailByUuid(routine1.timeRoutineData.uuid)
            assert(compareData1 == routine1)
        }
    }

    @Test
    fun deleteDayOfWeek_shouldReturnNull() {
        //요일만 삭제하는 로직은 도메인에 없는 동작. 현재 요일은 routine update로 처리됨.
    }
}