package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import software.seriouschoi.testutil.MainDispatcherRule
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.usecase.timeslot.DeleteTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.WatchTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.valid.GetTimeSlotPolicyValidUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.logic.TimeSlotAdjustHelper
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.toVo
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateHolder
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created by jhchoi on 2025. 11. 20.
 * jhchoi
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TimeSlotListPageViewModelTest {

    private val slotListFlow =
        MutableStateFlow<List<MetaEnvelope<TimeSlotVO>>>(emptyList())

    companion object {
        val testDayOfWeek = DayOfWeek.MONDAY
    }

    private lateinit var timeSlotEditStateHolder: TimeSlotEditStateHolder
    private lateinit var timeSlotListStateHolder: TimeSlotListStateHolder

    private lateinit var timeSlotAdjustHelper: TimeSlotAdjustHelper

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

        getPolicyValidUseCase = mock()
        deleteTimeSlotUseCase = mock()
        setTimeSlotUseCase = mock()
        setTimeSlotsUseCase = mock()
        watchTimeSlotListUseCase = mock()

        timeSlotAdjustHelper = TimeSlotAdjustHelper(
            getPolicyValidUseCase = getPolicyValidUseCase
        )

        runBlocking {
            whenever(setTimeSlotsUseCase.invoke(any(), any())).thenAnswer {
                DomainResult.Success(Unit)
            }
            whenever(deleteTimeSlotUseCase.invoke(any())).thenAnswer {
                DomainResult.Success(Unit)
            }
        }
        whenever(watchTimeSlotListUseCase.invoke(any())).thenAnswer {
            println("update slot list.")
            slotListFlow.map {
                DomainResult.Success(it)
            }
        }

        whenever(getPolicyValidUseCase.invoke(any())).thenAnswer {
            return@thenAnswer true
        }


        viewModel = TimeSlotListPageViewModel(
            watchTimeSlotListUseCase = watchTimeSlotListUseCase,
            setTimeSlotsUseCase = setTimeSlotsUseCase,
            setTimeSlotUseCase = setTimeSlotUseCase,
            deleteTimeSlotUseCase = deleteTimeSlotUseCase,

            timeSlotListStateHolder = timeSlotListStateHolder,
            timeSlotEditStateHolder = timeSlotEditStateHolder,
            timeSlotAdjustHelper = timeSlotAdjustHelper
        )

        viewModel.load(testDayOfWeek)
    }

    @Test
    fun test_showList() = runTest(dispatcherRule.dispatcher) {
        val testSlotList = listOf(
            TimeSlotVO(
                title = "6:00 slot",
                startTime = LocalTime.of(6, 0),
                endTime = LocalTime.of(7, 0)
            ),
            TimeSlotVO(
                title = "8:00 slot.",
                startTime = LocalTime.of(8, 0),
                endTime = LocalTime.of(9, 0)
            ),
            TimeSlotVO(
                title = "11:00 slot.",
                startTime = LocalTime.of(11, 0),
                endTime = LocalTime.of(6, 0)
            ),
        ).map {
            MetaEnvelope(it)
        }
        slotListFlow.update {
            testSlotList
        }
        advanceUntilIdle()

        viewModel.uiState.value.let { state ->
            val stateSlotList = state.slotListState.slotItemList.map {
                it.toVo()
            }
            assertEquals(testSlotList.map { it.payload }, stateSlotList, "목록이 예상과 다릅니다.")
        }
    }

    @Test
    fun test_selectTimeSlice_showEditTimeSlot() = runTest(dispatcherRule.dispatcher) {
        //비어 있는 5시를 선택.
        val newAddTimeHour = 5
        val sampleTime = LocalTime.of(newAddTimeHour, 0) to LocalTime.of(newAddTimeHour + 1, 0)

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
                title = "",
                startTime = sampleTime.first,
                endTime = sampleTime.second
            )

            assertEquals(expectedEditState.startTime, editState.startTime)
            assertEquals(expectedEditState.endTime, editState.endTime)
        }
    }


    @Test
    fun test_selectTimeSlice_showTimeSlotEdit() = runTest(dispatcherRule.dispatcher) {
        val beforeTime = TimeSlotVO(
            title = "5:00-6:00 slot",
            startTime = LocalTime.of(5, 0),
            endTime = LocalTime.of(6, 0)
        )
        val afterTime = TimeSlotVO(
            title = "9:00-10:00 slot.",
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0)
        )
        val testSlotList = listOf(
            beforeTime,
            afterTime,
        ).map {
            MetaEnvelope(it)
        }
        slotListFlow.update {
            testSlotList
        }
        advanceUntilIdle()

        //비어 있는 7시를 선택.
        val newAddTimeHour = 7
        val sampleTime = LocalTime.of(newAddTimeHour, 0) to LocalTime.of(newAddTimeHour + 1, 0)

        viewModel.sendIntent(
            TimeSlotListPageUiIntent.SelectTimeSlice(
                hourOfDay = newAddTimeHour
            )
        )
        advanceUntilIdle()

        // TODO: jhchoi 2025. 12. 2. 여기 테스트가 복잡한데.. 차라리 뷰모델에서의 복잡한 처리를 별개의 calculator를 만들고..
        /*
        그걸 테스트하자..
         */
        // 편집 화면 노출.
        viewModel.uiState.value.let { currentState ->
            val editState = currentState.editState
            assertTrue(editState != null)
            val expectedEditState = TimeSlotEditState(
                title = "",
                startTime = sampleTime.first,
                endTime = sampleTime.second,
                selectableStartTimeRange = beforeTime.endTime to sampleTime.second,
                selectableEndTimeRange = sampleTime.first to afterTime.startTime,
            )

            assertEquals(expectedEditState.startTime, editState.startTime, "편집 뷰의 시작시간이 예상과 다릅니다.")
            assertEquals(expectedEditState.endTime, editState.endTime, "편집 뷰의 종료시간이 예상과 다릅니다.")
            assertEquals(
                expectedEditState.selectableStartTimeRange,
                editState.selectableStartTimeRange,
                "편집뷰의 시작시간 범위가 예상과 다릅니다."
            )
            assertEquals(
                expectedEditState.selectableEndTimeRange,
                editState.selectableEndTimeRange,
                "편집뷰의 종료시간 범위가 예상과 다릅니다."
            )
        }
    }

    @Test
    fun test_selectTimeSlice_showTimeSlotEdit2() = runTest(dispatcherRule.dispatcher) {
        val sampleSlotList = listOf(
            TimeSlotVO(
                startTime = LocalTime.of(23, 0),
                endTime = LocalTime.of(5, 0),
                title = "11:00-5:00 slot"
            ),
            TimeSlotVO(
                startTime = LocalTime.of(5, 0),
                endTime = LocalTime.of(6, 0),
                title = "5:00-6:00 slot"
            ),
            //이 사이를 터치.
            TimeSlotVO(
                startTime = LocalTime.of(6, 45),
                endTime = LocalTime.of(8, 0),
                title = "6:45-8:00 slot"
            ),
            TimeSlotVO(
                startTime = LocalTime.of(8, 0),
                endTime = LocalTime.of(9, 0),
                title = "8:00-9:00 slot"
            )
        ).map {
            MetaEnvelope(it)
        }
        slotListFlow.update {
            sampleSlotList
        }
        val beforeSlot = sampleSlotList[1]
        val afterSlot = sampleSlotList[2]
        advanceUntilIdle()

        //비어 있는 6시를 선택.
        val newAddTimeHour = 6

        viewModel.sendIntent(
            TimeSlotListPageUiIntent.SelectTimeSlice(
                hourOfDay = newAddTimeHour
            )
        )
        advanceUntilIdle()

        // 편집 화면 노출.
        viewModel.uiState.value.let { currentState ->
            //6:00 - 6:45
            val sampleTime = LocalTime.of(newAddTimeHour, 0) to afterSlot.payload.startTime

            val editState = currentState.editState
            assertTrue(editState != null)
            val expectedEditState = TimeSlotEditState(
                title = "",
                startTime = sampleTime.first,
                endTime = sampleTime.second,
                selectableStartTimeRange = beforeSlot.payload.endTime to sampleTime.second,
                selectableEndTimeRange = sampleTime.first to afterSlot.payload.startTime,
            )

            //
            assertEquals(expectedEditState.startTime, editState.startTime, "편집 뷰의 시작시간이 예상과 다릅니다.")
            assertEquals(expectedEditState.endTime, editState.endTime, "편집 뷰의 종료시간이 예상과 다릅니다.")
            assertEquals(
                expectedEditState.selectableStartTimeRange,
                editState.selectableStartTimeRange,
                "편집뷰의 시작시간 범위가 예상과 다릅니다."
            )
            assertEquals(
                expectedEditState.selectableEndTimeRange,
                editState.selectableEndTimeRange,
                "편집뷰의 종료시간 범위가 예상과 다릅니다."
            )
        }
    }

    @Test
    fun test_selectTimeSlice_showTimeSlotEdit_selectableTimeOverMidnight() =
        runTest(dispatcherRule.dispatcher) {
            /*
            빈 시간을 선택,
            타임 슬롯 편집 노출,
            선택 가능 범위가 자정을 넘어갈때
             */
            val sampleSlotList = listOf(
                TimeSlotVO(
                    startTime = LocalTime.of(5, 0),
                    endTime = LocalTime.of(6, 0),
                    title = "5:00-6:00 slot"
                ),
                TimeSlotVO(
                    startTime = LocalTime.of(6, 45),
                    endTime = LocalTime.of(8, 0),
                    title = "6:45-8:00 slot"
                ),
                TimeSlotVO(
                    startTime = LocalTime.of(8, 0),
                    endTime = LocalTime.of(9, 0),
                    title = "8:00-9:00 slot"
                ),
                TimeSlotVO(
                    startTime = LocalTime.of(18, 0),
                    endTime = LocalTime.of(22, 0),
                    title = "18:00-22:00 slot"
                ),
                //이 사이를 터치.
            ).map {
                MetaEnvelope(it)
            }
            slotListFlow.update {
                sampleSlotList
            }
            val beforeSlot = sampleSlotList[3]
            val afterSlot = sampleSlotList[0]
            advanceUntilIdle()

            //비어 있는 23시를 선택.
            val newAddTimeHour = 23

            viewModel.sendIntent(
                TimeSlotListPageUiIntent.SelectTimeSlice(
                    hourOfDay = newAddTimeHour
                )
            )
            advanceUntilIdle()

            // 편집 화면 노출.
            viewModel.uiState.value.let { currentState ->
                //23:00 - 0:00
                val sampleTime = LocalTime.of(newAddTimeHour, 0).let {
                    it to it.plusHours(1)
                }

                val editState = currentState.editState
                assertTrue(editState != null)
                val expectedEditState = TimeSlotEditState(
                    title = "",
                    startTime = sampleTime.first,
                    endTime = sampleTime.second,
                    selectableStartTimeRange = beforeSlot.payload.endTime to sampleTime.second,
                    selectableEndTimeRange = sampleTime.first to afterSlot.payload.startTime,
                )

                //
                assertEquals(
                    expectedEditState.startTime,
                    editState.startTime,
                    "편집 뷰의 시작시간이 예상과 다릅니다."
                )
                assertEquals(expectedEditState.endTime, editState.endTime, "편집 뷰의 종료시간이 예상과 다릅니다.")
                assertEquals(
                    expectedEditState.selectableStartTimeRange,
                    editState.selectableStartTimeRange,
                    "편집뷰의 시작시간 범위가 예상과 다릅니다."
                )
                assertEquals(
                    expectedEditState.selectableEndTimeRange,
                    editState.selectableEndTimeRange,
                    "편집뷰의 종료시간 범위가 예상과 다릅니다."
                )
            }
        }
}