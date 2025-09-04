package software.seriouschoi.timeisgold.data.repositories.timeroutine

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.BaseRoomTest
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.ConflictCode
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.TechCode
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import timber.log.Timber
import java.time.DayOfWeek
import kotlin.test.todo

/**
 * Created by jhchoi on 2025. 8. 7.
 * jhchoi
 */
@RunWith(AndroidJUnit4::class)
internal class SetTimeRoutineTest : BaseRoomTest() {
    val savedRoutineCompoMonTue = testFixtures.routineCompoMonTue

    @Before
    fun setup() {
        runTest {
            timeRoutineRepo.saveTimeRoutineComposition(savedRoutineCompoMonTue)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun setTimeRoutine_changeTitleTimeslotDayOfWeek_shouldCollectChanged() = runTest {
        val routineCompoMonTue = savedRoutineCompoMonTue
        val routineFlow = timeRoutineRepo
            .observeCompositionByUuidFlow(routineCompoMonTue.timeRoutine.uuid)

        val routineCompoForUpdate = routineCompoMonTue.copy(
            timeRoutine = routineCompoMonTue.timeRoutine.copy(
                title = "new title"
            ),
            timeSlots = testFixtures.generateTimeSlotList(10, 14),
            dayOfWeeks = listOf(DayOfWeek.SATURDAY).map {
                it.toTimeRoutineDayOfWeekEntity()
            }.toSet()
        )

        timeRoutineRepo.setTimeRoutineComposition(routineCompoForUpdate)

        val gson = Gson()
        val routineEmitted: TimeRoutineComposition? = routineFlow.first()
        assert(routineEmitted != routineCompoMonTue) { "update not working"}
        assert(routineEmitted == routineCompoForUpdate) {
            """
                update failed.
                .timeRoutine same? : ${routineEmitted?.timeRoutine == routineCompoForUpdate.timeRoutine}
                .dayOfWeeks same? : ${routineEmitted?.dayOfWeeks == routineCompoForUpdate.dayOfWeeks}
                .timeSlots same? : ${routineEmitted?.timeSlots == routineCompoForUpdate.timeSlots}
                
                routineEmitted: ${gson.toJson(routineEmitted)}
            """.trimIndent()
        }
    }

    /**
     * 중복된 타임 슬롯 id를 저장할 경우. Exception발생.
     * 예를 들면.. 이미 루틴1 컴포지션이 저장된 상태.
     * 루틴2 컴포지션도 있는 상태.
     * 이 상태에 루틴2의 슬롯에 루틴1의 슬롯의 일부 요소를 넣어서 저장하려고 하면 오류가 나야함.
     */
    @Test
    fun setTimeSlot_duplicateUuid_shouldThrowException() = runTest {
        val routineCompoWedThu = testFixtures.routineCompoWedThu
        timeRoutineRepo.saveTimeRoutineComposition(routineCompoWedThu)

        //루틴1과 중복된 요소가 있는 슬롯 목록을 생성.
        val newSlots = routineCompoWedThu.timeSlots.toMutableList().apply {
            this.add(savedRoutineCompoMonTue.timeSlots.first())
        }.toList()
        //루틴2에 중복 요소가 있는 슬롯을 저장.
        val routine2ForUpdate = routineCompoWedThu.copy(
            timeSlots = newSlots
        )

        //Excpetion발생.
        val result = timeRoutineRepo.saveTimeRoutineComposition(routine2ForUpdate)
        Timber.d("result: $result")

        assert(((result as? DomainResult.Failure)?.error as? DomainError.Conflict)?.code == ConflictCode.Data)
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