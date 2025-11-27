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
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import software.seriouschoi.testutil.MainDispatcherRule
import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.usecase.timeslot.DeleteTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.NormalizeMinutesForUiUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.WatchTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.valid.GetTimeSlotPolicyValidUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.logic.TimeSlotCalculator
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
    // TODO: jhchoi 2025. 11. 20. 테스트 작성.

    private val slotListFlow =
        MutableStateFlow<List<MetaEnvelope<TimeSlotVO>>>(emptyList())

    companion object {
        val testDayOfWeek = DayOfWeek.MONDAY
        val sampleSlotList = listOf(
            TimeSlotVO(
                title = "wake up and prepare.",
                startTime = LocalTime.of(6, 0),
                endTime = LocalTime.of(7, 0)
            ),
            TimeSlotVO(
                title = "go to work",
                startTime = LocalTime.of(7, 0),
                endTime = LocalTime.of(8, 0)
            ),
            TimeSlotVO(
                title = "sleep",
                startTime = LocalTime.of(11, 0),
                endTime = LocalTime.of(6, 0)
            )
        )
    }

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

    private fun List<MetaEnvelope<TimeSlotVO>>.upsertSlot(
        slotId: String?,
        timeSlot: TimeSlotVO,
    ): Pair<List<MetaEnvelope<TimeSlotVO>>, MetaInfo> {
        val index = indexOfFirst { it.metaInfo.uuid == slotId }

        return if (index >= 0) {
            // update
            val target = this[index]
            val updated = target.copy(payload = timeSlot)
            val newList = toMutableList().apply { set(index, updated) }
            newList to updated.metaInfo
        } else {
            // insert
            val newSlot = MetaEnvelope(payload = timeSlot)
            (this + newSlot) to newSlot.metaInfo
        }
    }

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

        runBlocking {
            whenever(
                setTimeSlotUseCase.execute(
                    any(),
                    any(),
                    anyOrNull()
                )
            ).thenAnswer { invocation: InvocationOnMock ->
                val dayOfWeek = invocation.getArgument<DayOfWeek>(0)
                val timeSlot = invocation.getArgument<TimeSlotVO>(1)
                val slotId = invocation.getArgument<String?>(2)

                println("setTimeSlotUseCase called. vo=$timeSlot, dayOfWeek=$dayOfWeek, slotId=$slotId")

                val (newList, metaInfo) = slotListFlow.value.upsertSlot(slotId, timeSlot)
                slotListFlow.update { newList }

                DomainResult.Success(metaInfo)
            }
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
            timeSlotCalculator = timeSlotCalculator
        )

        viewModel.load(testDayOfWeek)
    }

    @Test
    fun test_addTimeSlot() = runTest(dispatcherRule.dispatcher) {
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
                slotUuid = null,
                title = "",
                startTime = sampleTime.first,
                endTime = sampleTime.second
            )
            assertEquals(expectedEditState, editState)
        }

        advanceUntilIdle()

        // TODO: jhchoi 2025. 11. 27.
        /*
        동작 시나리오를 정리해보자...
        slot edit이 추가되면.. 바로 저장...
        일단 slotEditState가 변경될때마다 저장하긴 하는데..

        일단 저장 시나리오로 가자.
         */
        //setTimeSlotUseCase 이 호출되면 안됨.
        val voCaptor = argumentCaptor<TimeSlotVO>()
        verify(setTimeSlotUseCase, times(1)).execute(
            dayOfWeek = anyOrNull(),
            timeSlot = voCaptor.capture(),
            slotId = anyOrNull()
        )

        advanceUntilIdle()

        // 목록에 아직 루틴추가 됨.
        viewModel.uiState.value.let { currentState ->
            val currentVoList = currentState.slotListState.slotItemList.map {
                TimeSlotVO(
                    startTime = LocalTimeUtil.create(it.startMinutesOfDay),
                    endTime = LocalTimeUtil.create(it.endMinutesOfDay),
                    title = it.title
                )
            }.toSet()
            val expectedList = setOf(voCaptor.firstValue)

            assertEquals(expectedList, currentVoList, "목록이 추가되지 않았습니다.")
        }
    }
}