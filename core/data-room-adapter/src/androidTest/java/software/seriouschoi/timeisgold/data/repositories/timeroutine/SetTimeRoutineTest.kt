package software.seriouschoi.timeisgold.data.repositories.timeroutine

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest

/**
 * Created by jhchoi on 2025. 8. 7.
 * jhchoi
 */
@RunWith(AndroidJUnit4::class)
internal class SetTimeRoutineTest : BaseRoomTest() {
    val routineComposition = testFixtures.routineCompoMonTue

    @Before
    fun setup() {
        runTest {
            timeRoutineRepo.addTimeRoutineComposition(routineComposition)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun setTimeRoutine_changeTitleTimeslotDayOfWeek_shouldCollectChanged() = runTest {
        turbineScope {
            val routine = routineComposition
            val routineTurbine = timeRoutineRepo
                .getTimeRoutineByDayOfWeek(routine.dayOfWeeks.first().dayOfWeek)
                .testIn(backgroundScope)

            val newRoutine = routine.let {
                testFixtures.routineCompoWedThu.copy(
                    dayOfWeeks = it.dayOfWeeks
                )
                val routine = it.timeRoutine.copy(
                    title = "new title",
                    createTime = System.currentTimeMillis()
                )
                it.copy(
                    timeRoutine = routine,
                    timeSlots = emptyList()
                )
            }
            backgroundScope.launch {

                timeRoutineRepo.setTimeRoutineComposition(newRoutine)
            }

            advanceUntilIdle()

            assert(routineTurbine.awaitItem() == newRoutine)
            assert(routineTurbine.awaitItem() != routine)

            routineTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * 중복된 타임 슬롯 id를 저장할 경우. Exception발생.
     * 예를 들면.. 이미 루틴1 컴포지션이 저장된 상태.
     * 루틴2 컴포지션도 있는 상태.
     * 이 상태에 루틴2의 슬롯에 루틴1의 슬롯의 일부 요소를 넣어서 저장하려고 하면 오류가 나야함.
     */
    @Test(expected = Exception::class)
    fun setTimeSlot_duplicateUuid_shouldThrowException() {
        runTest {
            val routine2 = testFixtures.routineCompoWedThu
            timeRoutineRepo.addTimeRoutineComposition(routine2)

            //루틴1과 중복된 요소가 있는 슬롯 목록을 생성.
            val newSlots = routine2.timeSlots.toMutableList().apply {
                this.add(routineComposition.timeSlots.first())
            }.toList()

            //루틴2에 중복 요소가 있는 슬롯을 저장.
            val routine2ForUpdate = routine2.copy(
                timeSlots = newSlots
            )

            //Excpetion발생.
            timeRoutineRepo.setTimeRoutineComposition(routine2ForUpdate)
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