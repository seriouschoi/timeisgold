package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
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

    private lateinit var viewModel: TimeRoutinePagerViewModel
    private lateinit var watchRoutineUseCase: WatchRoutineUseCase
    private lateinit var watchSelectableDayOfWeeksUseCase: WatchSelectableDayOfWeeksUseCase
    private lateinit var setRoutineUseCase: SetRoutineUseCase

    private lateinit var routineFlow: MutableStateFlow<DomainResult<MetaEnvelope<TimeRoutineVO>?>>
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

    @Test
    fun checkDayOfWeeks() = runTest(dispatcherRule.dispatcher) {
        val testDay = DayOfWeek.from(LocalDate.now())
        whenever(
            watchRoutineUseCase.invoke(testDay)
        ).thenReturn(routineFlow)

        whenever(
            watchSelectableDayOfWeeksUseCase.invoke(testDay)
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

        viewModel = TimeRoutinePagerViewModel(
            dayOfWeeksPagerStateHolder = DayOfWeeksPagerStateHolder(),
            routineTitleStateHolder = RoutineTitleStateHolder(),
            routineDayOfWeeksStateHolder = DayOfWeeksCheckStateHolder(),
            watchRoutineUseCase = watchRoutineUseCase,
            watchSelectableDayOfWeeksUseCase = watchSelectableDayOfWeeksUseCase,
            setRoutineUseCase = setRoutineUseCase
        )

        advanceUntilIdle()

        //월요일 체크.
        viewModel.sendIntent(
            TimeRoutinePagerUiIntent.CheckDayOfWeek(
                dayOfWeek = testDay,
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
                assertTrue(vo.dayOfWeeks.contains(testDay), "선택된 요일이 없습니다.")
            },
            check { dayOfWeek ->
                println("setRoutineUseCase.check - dayOfWeek=$dayOfWeek")
                assertTrue(dayOfWeek == testDay, "현재 요일이 아닙니다.")
            }
        )

        routineFlow.emit(DomainResult.Success(MetaEnvelope(testVo!!)))

        advanceUntilIdle()
        advanceUntilIdle()

        val uiState = viewModel.uiState.first()
        val checkedDayOfWeeks = uiState.routineDayOfWeeks.dayOfWeeksList.filter {
            it.checked
        }.map { it.dayOfWeek }.toSet()

        assertTrue(
            setOf(testDay) == checkedDayOfWeeks,
            "요일이 제대로 체크되지 않았습니다. 체크된_요일들=${checkedDayOfWeeks}"
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
