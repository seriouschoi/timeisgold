package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import software.seriouschoi.testutil.MainDispatcherRule
import software.seriouschoi.timeisgold.domain.usecase.timeslot.DeleteTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.NormalizeMinutesForUiUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.WatchTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.valid.GetTimeSlotPolicyValidUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.logic.TimeSlotCalculator
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateHolder
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created by jhchoi on 2025. 11. 20.
 * jhchoi
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TimeSlotListPageViewModelTest {
    // TODO: jhchoi 2025. 11. 20. 테스트 작성.

    private lateinit var timeSlotEditStateHolder: TimeSlotEditStateHolder
    private lateinit var timeSlotListStateHolder: TimeSlotListStateHolder

    private lateinit var timeSlotCalculator: TimeSlotCalculator

    // TODO: jhchoi 2025. 11. 20. 6개 usecase의 모킹이라.. 뭔가 잘못됐다.
    /*
    테스트를 진행하며, 이것들을 좀 별도 계층으로 만들어서, 해당 계층에 의존하게 만들어야...
     */
    private lateinit var normalizeMinutesForUiUseCase: NormalizeMinutesForUiUseCase
    private lateinit var getPolicyValidUseCase: GetTimeSlotPolicyValidUseCase
    private lateinit var deleteTimeSlotUseCase: DeleteTimeSlotUseCase
    private lateinit var setTimeSlotUseCase: SetTimeSlotUseCase
    private lateinit var setTimeSlotsUseCase: SetTimeSlotListUseCase
    private lateinit var watchTimeSlotListUseCase: WatchTimeSlotListUseCase


    private lateinit var viewModel: TimeSlotListPageViewModel

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {

        timeSlotEditStateHolder = TimeSlotEditStateHolder()
        timeSlotListStateHolder = TimeSlotListStateHolder()


        normalizeMinutesForUiUseCase = mock()
        getPolicyValidUseCase = mock()
        deleteTimeSlotUseCase = mock()
        setTimeSlotUseCase = mock()
        setTimeSlotsUseCase = mock()
        watchTimeSlotListUseCase = mock()

        timeSlotCalculator = TimeSlotCalculator(
            normalizeMinutesForUiUseCase = normalizeMinutesForUiUseCase,
            getPolicyValidUseCase = getPolicyValidUseCase
        )

        // TODO: jhchoi 2025. 11. 20. whenever로 모킹 구현.

        viewModel = TimeSlotListPageViewModel(
            watchTimeSlotListUseCase = watchTimeSlotListUseCase,
            setTimeSlotsUseCase = setTimeSlotsUseCase,
            setTimeSlotUseCase = setTimeSlotUseCase,
            deleteTimeSlotUseCase = deleteTimeSlotUseCase,
            timeSlotListStateHolder = timeSlotListStateHolder,
            timeSlotEditStateHolder = timeSlotEditStateHolder,
            timeSlotCalculator = timeSlotCalculator
        )
    }

    @Test
    fun test() = runTest(dispatcherRule.dispatcher) {
        // TODO: jhchoi 2025. 11. 20.
        /*
        빈 공간을 터치해서 슬롯을 추가하는걸 먼저 해보자.
         */
        val newAddTimeHour = 5
        viewModel.sendIntent(
            TimeSlotListPageUiIntent.SelectTimeSlice(
                hourOfDay = newAddTimeHour
            )
        )
        advanceUntilIdle()

        /*
        edit이 켜지겠지..?
        그리고 아마 setTimeSlot도 동작할꺼야...
         */

        /*
        이게 문제같아...난 지금 이걸 완전히 새로 만들어야할수도 있어..
        모든 기능들이 정책없이 일단 만들자하고 만들다보니..
        폭주된 상태야..

        뭐가 어떻게 흘러갈지 암시적인 흐름에 맡긴 상태이지...
        이 테스트에서 모든 흐름을 명시적으로 다시 만들어야해.
         */

        /*
        그나마 다행인건..  state는 비교적 정상...이 아니군..
        state {
            editState,
            listState {
                list,
                loadingMessage,
                errorMessage
            }
        }
        이게..아마 처음에 목록 상태에서 오류랑 에러를 표시했는데... 로딩과 오류는 editState와 상관없이 돌아가야 할것 같다.
         */
        viewModel.uiState.value.let { currentUiState ->

        }
    }

    @Test
    fun addTimeSlot() = runTest(dispatcherRule.dispatcher) {
        //비어 있는 5시를 선택.
        val newAddTimeHour = 5
        viewModel.sendIntent(
            TimeSlotListPageUiIntent.SelectTimeSlice(
                hourOfDay = newAddTimeHour
            )
        )
        advanceUntilIdle()

        // 편집 화면 노출.
        viewModel.uiState.value.let { currentState ->
            val editState = currentState.editState
            assertTrue(editState != null)

            val expectedEditState = TimeSlotEditState(
                slotUuid = null,
                title = "",
                startTime = LocalTime.of(newAddTimeHour, 0),
                endTime = LocalTime.of(newAddTimeHour + 1, 0)
            )
            assertEquals(editState, expectedEditState)
        }

        advanceUntilIdle()

        //setTimeSlotUseCase 이 호출되면 안됨.
        verify(setTimeSlotUseCase, never())
            .execute(any(), any(), any())

        //setTimeSlotsUseCase 가 호출되서도 안됨.
        verify(setTimeSlotsUseCase, never())
            .invoke(any(), any())

        // 목록에 아직 루틴이 있으면 안됨.
        viewModel.uiState.value.let { currentState ->
            val currentList = currentState.slotListState.slotItemList
            val expectedList = emptyList<TimeSlotItemUiState>()
            assertEquals(currentList, expectedList)
        }
    }
}