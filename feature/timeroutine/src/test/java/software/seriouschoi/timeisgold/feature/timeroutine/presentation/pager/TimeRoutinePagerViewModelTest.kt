package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import software.seriouschoi.testutil.MainDispatcherRule
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchSelectableDayOfWeeksUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.DayOfWeeksCheckStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager.DayOfWeeksPagerStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineTitleStateHolder
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by jhchoi on 2025. 11. 18.
 * jhchoi
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TimeRoutinePagerViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val testDay1 = DayOfWeek.from(LocalDate.now())

    private lateinit var watchRoutineUseCase: WatchRoutineUseCase
    private lateinit var watchSelectableDayOfWeeksUseCase: WatchSelectableDayOfWeeksUseCase

    private lateinit var routineFlow: MutableStateFlow<DomainResult<MetaEnvelope<TimeRoutineVO>?>>

    private lateinit var setRoutineUseCase: SetRoutineUseCase
    private lateinit var selectableDayOfWeeksFlow: Flow<DomainResult<List<DayOfWeek>>>

    @Before
    fun setup() {
        //초기 루틴. 기존 루틴 없음.
        routineFlow = MutableStateFlow(DomainResult.Success(null))
        selectableDayOfWeeksFlow =
            MutableStateFlow(DomainResult.Success(DayOfWeek.entries.toList()))

        watchRoutineUseCase = Mockito.mock()
        watchSelectableDayOfWeeksUseCase = Mockito.mock()
        setRoutineUseCase = Mockito.mock()
    }

    private suspend fun generateViewModel(): TimeRoutinePagerViewModel {
        whenever(
            watchRoutineUseCase.invoke(testDay1)
        ).thenReturn(routineFlow)

        whenever(
            watchSelectableDayOfWeeksUseCase.invoke(testDay1)
        ).thenReturn(
            selectableDayOfWeeksFlow
        )
        whenever(setRoutineUseCase.invoke(any(), any())).thenReturn(
            DomainResult.Success(
                MetaInfo(
                    uuid = "mock_uuid",
                    createTime = Instant.now()
                )
            )
        )

        return TimeRoutinePagerViewModel(
            dayOfWeeksPagerStateHolder = DayOfWeeksPagerStateHolder(),
            routineTitleStateHolder = RoutineTitleStateHolder(),
            routineDayOfWeeksStateHolder = DayOfWeeksCheckStateHolder(),
            watchRoutineUseCase = watchRoutineUseCase,
            watchSelectableDayOfWeeksUseCase = watchSelectableDayOfWeeksUseCase,
            setRoutineUseCase = setRoutineUseCase
        )
    }

    @Test
    fun test_checkDayOfWeek() = runTest(dispatcherRule.dispatcher) {
        val viewModel = generateViewModel()

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
        var testVo: TimeRoutineVO? = null
        println("setRoutineUseCase.isMock = ${Mockito.mockingDetails(setRoutineUseCase).isMock}")
        verify(setRoutineUseCase).invoke(
            check { vo ->
                println("setRoutineUseCase.check - vo=$vo")
                testVo = vo
                assertTrue(vo.dayOfWeeks.contains(testDay1), "선택된 요일이 없습니다.")
            },
            check { dayOfWeek ->
                println("setRoutineUseCase.check - dayOfWeek=$dayOfWeek")
                assertTrue(dayOfWeek == testDay1, "현재 요일이 아닙니다.")
            }
        )

        routineFlow.emit(DomainResult.Success(MetaEnvelope(testVo!!)))

        advanceUntilIdle()

        val uiState = viewModel.uiState.first()
        val checkedDayOfWeeks = uiState.routineDayOfWeeks.dayOfWeeksList.filter {
            it.checked
        }.map { it.dayOfWeek }.toSet()

        assertTrue(
            setOf(testDay1) == checkedDayOfWeeks,
            "요일이 제대로 체크되지 않았습니다. 체크된_요일들=${checkedDayOfWeeks}"
        )
    }

    @Test
    fun test_uncheckDayOfWeek() = runTest(dispatcherRule.dispatcher) {
        val viewModel = generateViewModel()

        //초기화.
        advanceUntilIdle()

        val defaultRoutine = TimeRoutineVO(
            title = "test routine",
            dayOfWeeks = setOf(testDay1)
        )

        //기본 루틴 추가.
        routineFlow.emit(MetaEnvelope(defaultRoutine).let {
            DomainResult.Success(it)
        })

        //갱신.
        advanceUntilIdle()

        //기본 루틴 정상 출력 확인.
        var currentUiState = viewModel.uiState.first()
        assertTrue(currentUiState.titleState.title == defaultRoutine.title, "루틴 제목이 잘못됐습니다.")
        var currentRoutineDayOfWeeks = currentUiState.routineDayOfWeeks.dayOfWeeksList.filter {
            it.enabled && it.checked
        }.map { it.dayOfWeek }
        println("루틴 타이틀=${currentUiState.titleState.title}")
        println("루틴 요일=${currentRoutineDayOfWeeks}")
        assertTrue(
            currentRoutineDayOfWeeks.toSet() == defaultRoutine.dayOfWeeks,
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
                assertTrue(dayOfWeek == testDay1, "현재 요일이 아닙니다.")
            }
        )

        //루틴 삭제됨.
        routineFlow.emit(DomainResult.Success(null))
        println("루틴을 삭제했습니다.")

        advanceUntilIdle()

        currentUiState = viewModel.uiState.value
        currentRoutineDayOfWeeks = currentUiState.routineDayOfWeeks.dayOfWeeksList.filter {
            it.enabled && it.checked
        }.map { it.dayOfWeek }
        assertTrue(currentRoutineDayOfWeeks.isEmpty(), "루틴이 요일이 남아있습니다.")

        var routineTitleState = currentUiState.titleState.title
        assertTrue(routineTitleState == "", "루텐 제목이 남아있습니다. $routineTitleState")
    }
}