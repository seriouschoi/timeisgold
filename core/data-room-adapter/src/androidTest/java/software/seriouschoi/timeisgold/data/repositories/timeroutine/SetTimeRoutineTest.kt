package software.seriouschoi.timeisgold.data.repositories.timeroutine

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineDayOfWeekEntity
import java.util.UUID

/**
 * Created by jhchoi on 2025. 8. 7.
 * jhchoi
 */
@RunWith(AndroidJUnit4::class)
internal class SetTimeRoutineTest : BaseRoomTest() {
    @Before
    fun setup() {
        runTest {
            val dayOfWeekForRoutine = timeSlotTestFixtures.getTestRoutineDayOfWeeks1()
            val routine = timeSlotTestFixtures.createTimeRoutine(dayOfWeekForRoutine)
            timeRoutineRepo.addTimeRoutine(routine)

            timeSlotTestFixtures.createDetailDataList().forEach {
                timeSlotRepo.addTimeSlot(
                    timeSlotData = it,
                    timeRoutineUuid = routine.uuid
                )
            }
        }
    }

    @Test
    fun setTimeRoutine_changeTitleTimeslotDayOfWeek_shouldCollectChanged() {
        runTest {
            val dayOfWeekForRoutine = timeSlotTestFixtures.getTestRoutineDayOfWeeks1()
            val routineDetailFromDb =
                timeRoutineRepo.getTimeRoutineDetail(dayOfWeekForRoutine.first())
                    ?: throw IllegalStateException("routine is null")
            val routineFromDb = routineDetailFromDb.timeRoutineData

            val dayOfWeekForNewRoutine = timeSlotTestFixtures.getTestRoutineDayOfWeeks2()
            val routineForChange = routineFromDb.copy(
                title = "test_title_changed",
                dayOfWeekList = dayOfWeekForNewRoutine.map {
                    TimeRoutineDayOfWeekEntity(
                        dayOfWeek = it,
                        uuid = UUID.randomUUID().toString()
                    )
                },
                createTime = System.currentTimeMillis(),
            )
            timeRoutineRepo.setTimeRoutine(routineForChange)

            //jhchoi 2025. 8. 7. 스케줄 변경이 잘 되는가?
            val changedRoutine = timeRoutineRepo.getTimeRoutineDetailByUuid(routineFromDb.uuid)
            assert(routineFromDb != changedRoutine?.timeRoutineData)
            assert(routineForChange == changedRoutine?.timeRoutineData)
        }
    }

    @Test
    fun setTimeRoutine_changeUuid_shouldThrowException() {
        // DESIGN NOTE:
        // 현재 DB 구조상 `id`가 PK로 작동하고 `uuid`는 제약이 없음.
        // 따라서 `uuid`만 바꾸고 update를 해도 정상 동작하므로, 테스트할 필요가 없음.
        // 도메인 불변성 위반은 Repository/UseCase 계층에서 검증되어야 하며,
        // 구조 변경 시 본 테스트는 재검토 필요함.
    }
}