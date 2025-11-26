package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.check
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import software.seriouschoi.testutil.MainDispatcherRule
import software.seriouschoi.timeisgold.core.common.util.CurrentDayOfWeekProviderPort
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchSelectableDayOfWeeksUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.DayOfWeeksCheckStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.getActiveCheckDayOfWeeks
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.getActiveDayOfWeeks
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager.DayOfWeeksPagerStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineTitleStateHolder
import java.time.DayOfWeek
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created by jhchoi on 2025. 11. 18.
 * jhchoi
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TimeRoutinePagerViewModelTest {
    // TODO: jhchoi 2025. 11. 20. 참고할 테스트

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val testDay1 = DayOfWeek.MONDAY
    private val testDay2 = DayOfWeek.TUESDAY
    private val defaultDay = testDay1

    private lateinit var watchRoutineUseCase: WatchRoutineUseCase
    private lateinit var watchSelectableDayOfWeeksUseCase: WatchSelectableDayOfWeeksUseCase
    private lateinit var setRoutineUseCase: SetRoutineUseCase

    private lateinit var routineFlow: MutableStateFlow<TimeRoutineVO?>

    private lateinit var viewModel: TimeRoutinePagerViewModel

    @Before
    fun setup() {
        //초기 루틴. 기존 루틴 없음.
        routineFlow = MutableStateFlow(null)

        watchRoutineUseCase = mock()
        watchSelectableDayOfWeeksUseCase = mock()
        setRoutineUseCase = mock()

        val currentDayOfWeekProvider: CurrentDayOfWeekProviderPort = mock()
        whenever(currentDayOfWeekProvider.getCurrentDayOfWeek()).thenAnswer {
            defaultDay
        }

        whenever(
            watchRoutineUseCase.invoke(any<DayOfWeek>())
        ).thenAnswer { invocation ->
            val param = invocation.getArgument<DayOfWeek>(0)
            routineFlow.map { routine ->
                val envelope = routine
                    ?.takeIf { param in it.dayOfWeeks }
                    ?.let { MetaEnvelope(it) }
                DomainResult.Success(envelope)
            }
        }

        whenever(
            watchSelectableDayOfWeeksUseCase.invoke(any<DayOfWeek>())
        ).thenAnswer { invocation ->
            val param = invocation.getArgument<DayOfWeek>(0)

            /*
            현재요일이 아닌 루틴의 요일들을 제외하고 활성.
             */
            val allRoutineDayOfWeeks = routineFlow.map { it?.dayOfWeeks ?: emptySet() }
            val currentRoutineDayOfWeeks = routineFlow.map {
                it?.takeIf { it.dayOfWeeks.contains(param) }?.dayOfWeeks ?: emptySet()
            }
            combine(
                allRoutineDayOfWeeks,
                currentRoutineDayOfWeeks
            ) { usedDayOfWeeks, currentDayOfWeeks ->
                DayOfWeek.entries.filter { day ->
                    val usedByOtherRoutine = usedDayOfWeeks.contains(day)
                    val usedByCurrentRoutine = currentDayOfWeeks.contains(day)
                    !usedByOtherRoutine || usedByCurrentRoutine
                }
            }.map {
                DomainResult.Success(it)
            }
        }


        runBlocking {
            whenever(setRoutineUseCase.invoke(any(), any())).thenReturn(
                DomainResult.Success(
                    MetaInfo(
                        uuid = "mock_uuid",
                        createTime = Instant.now()
                    )
                )
            )
        }

        viewModel = TimeRoutinePagerViewModel(
            dayOfWeeksPagerStateHolder = DayOfWeeksPagerStateHolder(),
            routineTitleStateHolder = RoutineTitleStateHolder(),
            routineDayOfWeeksStateHolder = DayOfWeeksCheckStateHolder(),
            watchRoutineUseCase = watchRoutineUseCase,
            watchSelectableDayOfWeeksUseCase = watchSelectableDayOfWeeksUseCase,
            setRoutineUseCase = setRoutineUseCase,
            currentDayOfWeekProviderPort = currentDayOfWeekProvider
        )
    }

    /**
     * 요일 선택
     */
    @Test
    fun test_checkDayOfWeek() = runTest(dispatcherRule.dispatcher) {
        advanceUntilIdle()

        //월요일 체크.
        viewModel.sendIntent(
            TimeRoutinePagerUiIntent.CheckDayOfWeek(
                dayOfWeek = testDay1,
                isCheck = true
            )
        )

        advanceUntilIdle()

        //루틴 저장 useCase실행됨.
        val voCaptor = argumentCaptor<TimeRoutineVO>()
        val dayOfWeekCaptor = argumentCaptor<DayOfWeek>()
        verify(setRoutineUseCase).invoke(
            routineVO = voCaptor.capture(),
            dayOfWeek = dayOfWeekCaptor.capture()
        )
        voCaptor.let {
            val captureVo = it.firstValue
            assertTrue(captureVo.dayOfWeeks.contains(testDay1), "선택된 요일이 없습니다.")
        }
        dayOfWeekCaptor.let {
            val captureDayOfWeek = it.firstValue
            assertEquals(captureDayOfWeek, defaultDay, "현재 요일이 아닙니다.")
        }

        routineFlow.emit(voCaptor.firstValue)

        advanceUntilIdle()

        val uiState = viewModel.uiState.first()
        val checkedDayOfWeeks = uiState.routineDayOfWeeks.getActiveCheckDayOfWeeks()
        assertEquals(
            uiState.routineDayOfWeeks.getActiveCheckDayOfWeeks(),
            listOf(testDay1),
            "요일이 제대로 체크되지 않았습니다. 체크된_요일들=${checkedDayOfWeeks}"
        )
    }

    /**
     * 요일 선택 해제.
     */
    @Test
    fun test_uncheckDayOfWeek() = runTest(dispatcherRule.dispatcher) {

        //초기화.
        advanceUntilIdle()

        val defaultRoutine = TimeRoutineVO(
            title = "test routine",
            dayOfWeeks = setOf(testDay1)
        )

        //기본 루틴 추가.
        routineFlow.emit(defaultRoutine)

        //갱신.
        advanceUntilIdle()

        //기본 루틴 정상 출력 확인.
        var currentUiState = viewModel.uiState.first()
        assertEquals(currentUiState.titleState.title, defaultRoutine.title, "루틴 제목이 잘못됐습니다.")
        var currentRoutineDayOfWeeks = currentUiState.routineDayOfWeeks.dayOfWeeksList.filter {
            it.enabled && it.checked
        }.map { it.dayOfWeek }
        println("루틴 타이틀=${currentUiState.titleState.title}")
        println("루틴 요일=${currentRoutineDayOfWeeks}")
        assertEquals(
            currentRoutineDayOfWeeks.toSet(),
            defaultRoutine.dayOfWeeks,
            "기본 루틴이 정상 추가되지 않았습니다."
        )

        //체크 해제.
        viewModel.sendIntent(
            TimeRoutinePagerUiIntent.CheckDayOfWeek(
                dayOfWeek = testDay1,
                isCheck = false
            )
        )

        println("요일 체크를 해제합니다.")
        advanceUntilIdle()

        //루틴 저장 useCase실행됨.(삭제)
        verify(setRoutineUseCase).invoke(
            check { vo ->
                assertTrue(vo.dayOfWeeks.isEmpty(), "선택이 해제되지 않았습니다.")
            },
            check { dayOfWeek ->
                assertEquals(dayOfWeek, testDay1, "현재 요일이 아닙니다.")
            }
        )

        //루틴 삭제됨.
        routineFlow.emit(null)
        println("루틴을 삭제했습니다.")

        advanceUntilIdle()

        currentUiState = viewModel.uiState.value
        currentRoutineDayOfWeeks = currentUiState.routineDayOfWeeks.getActiveCheckDayOfWeeks()
        assertTrue(currentRoutineDayOfWeeks.isEmpty(), "루틴이 요일이 남아있습니다.")

        val routineTitleState = currentUiState.titleState.title
        assertEquals(routineTitleState, "", "루텐 제목이 남아있습니다. $routineTitleState")
    }

    /**
     * 여러개의 요일 선택.
     */
    @Test
    fun test_checkMultipleDayOfWeeks() = runTest(dispatcherRule.dispatcher) {
        /*
        여러 요일을 선택시 현재 루틴으로 체크 되는가?
         */
        advanceUntilIdle()

        val voCaptor = argumentCaptor<TimeRoutineVO>()
        val dayOfWeekCaptor = argumentCaptor<DayOfWeek>()

        //기본 요일 선택.
        routineFlow.emit(
            TimeRoutineVO(
                title = "test routine",
                dayOfWeeks = setOf(testDay1)
            )
        )
        advanceUntilIdle()


        //day 1 선택 확인.
        viewModel.uiState.let {
            val uiState = it.value
            assertTrue(
                uiState.routineDayOfWeeks.getActiveCheckDayOfWeeks().containsAll(
                    listOf(testDay1)
                ),
                "testDay1 선택 실패."
            )
        }

        //day 2 선택.
        viewModel.sendIntent(
            TimeRoutinePagerUiIntent.CheckDayOfWeek(
                dayOfWeek = testDay2,
                isCheck = true
            )
        )
        advanceUntilIdle()

        //day2 선택 usecase호출 확인.
        verify(setRoutineUseCase, times(1)).invoke(
            routineVO = voCaptor.capture(),
            dayOfWeek = dayOfWeekCaptor.capture()
        )
        voCaptor.let {
            val captureVo = it.firstValue
            assertTrue(captureVo.dayOfWeeks.containsAll(listOf(testDay1, testDay2)))
        }
        dayOfWeekCaptor.let {
            val captureDayOfWeek = it.firstValue
            assertEquals(captureDayOfWeek, testDay1, "현재 요일이 아닙니다.")
        }

        routineFlow.emit(voCaptor.firstValue)

        advanceUntilIdle()

        //day 2 선택 확인.
        viewModel.uiState.let {
            val uiState = it.value
            println("uiState=$uiState")
            assertTrue(
                uiState.routineDayOfWeeks.getActiveCheckDayOfWeeks().containsAll(
                    listOf(testDay1, testDay2)
                ),
                "testDay2 선택 실패."
            )
        }
    }

    /**
     * 동작: 현재 요일이 아닌 다른 요일 선택 해제.
     * 기대: 현재 요일만 남아있음.
     */
    @Test
    fun test_uncheckOtherDayOfWeek() = runTest(dispatcherRule.dispatcher) {
        //init.
        val defaultVo = TimeRoutineVO(
            title = "test routine",
            dayOfWeeks = setOf(testDay1, testDay2)
        )

        routineFlow.emit(defaultVo)
        advanceUntilIdle()

        //uncheck.
        viewModel.sendIntent(
            TimeRoutinePagerUiIntent.CheckDayOfWeek(
                dayOfWeek = testDay2,
                isCheck = false
            )
        )
        advanceUntilIdle()

        val voCaptor = argumentCaptor<TimeRoutineVO>()
        val dayOfWeekCaptor = argumentCaptor<DayOfWeek>()
        verify(setRoutineUseCase).invoke(
            routineVO = voCaptor.capture(),
            dayOfWeek = dayOfWeekCaptor.capture()
        )
        voCaptor.let {
            val vo = it.firstValue
            assertEquals(vo.dayOfWeeks, setOf(testDay1), "요일 해제 실패.")

            routineFlow.emit(vo)
        }
        dayOfWeekCaptor.let {
            val dayOfWeek = it.firstValue
            assertEquals(dayOfWeek, testDay1, "현재 요일이 아닙니다.")
        }

        advanceUntilIdle()

        viewModel.uiState.let {
            val uiState = it.value
            val currentDayOfWeeks = uiState.routineDayOfWeeks.getActiveCheckDayOfWeeks()
            assertEquals(currentDayOfWeeks, listOf(testDay1), "요일이 해제되지 않았습니다.")
        }
    }

    /**
     * 동작: 현재 요일 선택 해제.
     * 기대: 현재 요일이 아닌 다른 요일이 비활성 체크 상태로 전환.
     */
    @Test
    fun test_uncheckCurrentDayOfWeek() = runTest(dispatcherRule.dispatcher) {
        val today = testDay1
        val disableDay = testDay1
        val remainDay = testDay2
        //init.
        val defaultVo = TimeRoutineVO(
            title = "test routine",
            dayOfWeeks = setOf(disableDay, remainDay)
        )

        routineFlow.emit(defaultVo)

        advanceUntilIdle()

        //현재 요일 체크 해제.
        viewModel.sendIntent(
            TimeRoutinePagerUiIntent.CheckDayOfWeek(
                dayOfWeek = disableDay,
                isCheck = false
            )
        )
        advanceUntilIdle()

        val voCaptor = argumentCaptor<TimeRoutineVO>()
        val dayOfWeekCaptor = argumentCaptor<DayOfWeek>()
        verify(setRoutineUseCase).invoke(
            routineVO = voCaptor.capture(),
            dayOfWeek = dayOfWeekCaptor.capture()
        )
        voCaptor.let {
            val vo = it.firstValue
            assertEquals(vo.dayOfWeeks, setOf(remainDay), "요일 해제 요청 실패.")

            routineFlow.emit(vo)
        }
        dayOfWeekCaptor.let {
            val dayOfWeek = it.firstValue
            assertEquals(dayOfWeek, today, "현재 요일이 아닙니다.")
        }

        advanceUntilIdle()

        viewModel.uiState.let { stateFlow ->
            val uiState = stateFlow.value
            val currentDayOfWeeks = uiState.routineDayOfWeeks.getActiveCheckDayOfWeeks()
            println("currentDayOfWeeks=$currentDayOfWeeks")
            assertTrue(!currentDayOfWeeks.contains(disableDay), "요일이 해제되지 않았습니다.")

            val currentTitle = uiState.titleState.title
            println("currentTitle=$currentTitle")
            assertEquals(currentTitle, "", "루틴 제목이 해제되지 않았습니다.")

            val activeDayOfWeeks = uiState.routineDayOfWeeks.getActiveDayOfWeeks()
            val expectedActiveDayOfWeeks = DayOfWeek.entries.filter {
                it != remainDay
            }
            println("expectedActiveDayOfWeeks=$expectedActiveDayOfWeeks")
            assertEquals(activeDayOfWeeks, expectedActiveDayOfWeeks, "잘못된 활성된 요일이 활성되었습니다.")
        }
    }

    /**
     * 제목 입력시 루틴 생성.
     */
    @Test
    fun test_inputTitle_makeRoutine() = runTest {
        //기본 값.
        routineFlow.emit(
            null
        )
        advanceUntilIdle()

        //루틴 없음 상태 확인.
        viewModel.uiState.let {
            val state = it.value
            val titleState = state.titleState.title
            val routineDayOfWeeks = state.routineDayOfWeeks.getActiveCheckDayOfWeeks()
            assertTrue(titleState.isEmpty(), "루틴 제목이 잘못됐습니다.")
            assertTrue(routineDayOfWeeks.isEmpty(), "루틴 요일이 잘못됐습니다.")
        }

        //제목 입력.
        val testRoutineTitle = "test routine"
        viewModel.sendIntent(
            TimeRoutinePagerUiIntent.InputRoutineTitle(
                title = testRoutineTitle
            )
        )
        advanceUntilIdle()

        //루틴 변경 요청 확인.
        val voCaptor = argumentCaptor<TimeRoutineVO>()
        val dayOfWeekCaptor = argumentCaptor<DayOfWeek>()
        verify(setRoutineUseCase).invoke(
            routineVO = voCaptor.capture(),
            dayOfWeek = dayOfWeekCaptor.capture()
        )
        voCaptor.let {
            val vo = it.firstValue
            assertEquals(vo.title, testRoutineTitle, "루틴 제목이 잘못됐습니다.")
            assertEquals(vo.dayOfWeeks, setOf(defaultDay), "오늘 날짜 루틴으로 요청되지 않았습니다.")
            routineFlow.emit(vo)
        }
        advanceUntilIdle()

        //변경된 루틴 반영 확인.
        viewModel.uiState.let {
            val state = it.value
            val titleState = state.titleState.title
            val routineDayOfWeeks = state.routineDayOfWeeks.getActiveCheckDayOfWeeks()
            assertEquals(titleState, testRoutineTitle, "루틴 제목이 잘못됐습니다.")
            assertEquals(routineDayOfWeeks, listOf(defaultDay), "루틴 요일이 잘못됐습니다.")
        }
    }

    /**
     * 동작: 비어있는 다른 요일 선택.
     * 기대:
     * 현재 선택된 루틴 없음.
     * 타이틀 없음.
     * 현재 선택된 활성 요일 없음.
     */
    @Test
    fun test_selectEmptyRoutineDay_showEmptyState() = runTest(dispatcherRule.dispatcher) {
        val fromDay = testDay1
        val destDay = testDay2

        //기본 루틴.
        routineFlow.emit(
            TimeRoutineVO(
                title = "test routine",
                dayOfWeeks = setOf(fromDay)
            )
        )
        //기본 요일.
        viewModel.sendIntent(
            TimeRoutinePagerUiIntent.SelectCurrentDayOfWeek(
                currentDayOfWeek = fromDay
            )
        )
        advanceUntilIdle()

        //요일 변경.
        viewModel.sendIntent(
            TimeRoutinePagerUiIntent.SelectCurrentDayOfWeek(
                currentDayOfWeek = destDay
            )
        )

        advanceUntilIdle()

        //요일 상태 확인.
        viewModel.uiState.let { stateFlow ->
            val state = stateFlow.value

            //다른 요일이라 비활성되었는데 체크된 요일들.
            val disableCheckedDayOfWeeks = state.routineDayOfWeeks.dayOfWeeksList.filter {
                !it.enabled && it.checked
            }.map {
                it.dayOfWeek
            }

            val expectedDisableCheckedDayOfWeeks = listOf(fromDay)
            assertEquals(
                disableCheckedDayOfWeeks,
                expectedDisableCheckedDayOfWeeks,
                "잘못된 요일이 비활성 되었습니다."
            )

            val currentRoutineDayOfWeek = state.routineDayOfWeeks.getActiveCheckDayOfWeeks()
            val expectedCurrentRoutineDayOfWeek = emptyList<DayOfWeek>()
            assertEquals(
                currentRoutineDayOfWeek,
                expectedCurrentRoutineDayOfWeek,
                "잘못된 요일이 체크 되었습니다."
            )

            val currentTitle = state.titleState.title
            assertEquals(currentTitle, "", "루틴 제목이 잘못됐습니다.")
        }
    }

    /**
     * 동작: 루틴이 있는 요일 선택.
     * 기대:
     * 현재 선택된 루틴 있음.
     * 타이틀 있음.
     * 현재 선택된 활성 요일 있음.
     */
    @Test
    fun test_fromEmptyDay_changeRoutineDay_showRoutineState() = runTest(dispatcherRule.dispatcher) {
        val fromDay = testDay1
        val destDay = testDay2

        val testRoutine = TimeRoutineVO(
            title = "test routine",
            dayOfWeeks = setOf(destDay)
        )

        //기본 루틴.
        routineFlow.emit(
            testRoutine
        )

        advanceUntilIdle()

        //기본 요일 설정.
        viewModel.sendIntent(
            TimeRoutinePagerUiIntent.SelectCurrentDayOfWeek(
                currentDayOfWeek = fromDay
            )
        )
        advanceUntilIdle()

        //상태 확인.
        viewModel.uiState.let { stateFlow ->
            val state = stateFlow.value
            assertEquals(
                state.titleState.title,
                "",
                "현재 루틴이 없는데 제목이 출력되고 있습니다."
            )
            assertTrue(
                state.routineDayOfWeeks.getActiveCheckDayOfWeeks().isEmpty(),
                "현재 루틴이 없는데, 선택된 요일이 출력되고 있습니다."
            )
        }

        //루틴 요일로 변경.
        viewModel.sendIntent(
            TimeRoutinePagerUiIntent.SelectCurrentDayOfWeek(
                currentDayOfWeek = destDay
            )
        )
        advanceUntilIdle()

        //상태 확인.
        viewModel.uiState.let { stateFlow ->
            val state = stateFlow.value
            assertEquals(
                state.titleState.title,
                testRoutine.title,
                "루틴 제목을 불러오지 못했습니다."
            )
            assertEquals(
                state.routineDayOfWeeks.getActiveCheckDayOfWeeks().toSet(),
                testRoutine.dayOfWeeks,
                "루틴 요일을 불러오지 못했습니다."
            )
        }
    }
}