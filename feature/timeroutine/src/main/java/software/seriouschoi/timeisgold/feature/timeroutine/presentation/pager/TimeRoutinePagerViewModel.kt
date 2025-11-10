package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.withResultStateLifecycle
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.domain.mapper.asResultState
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetRoutineUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.data.TimeRoutineFeatureState
import software.seriouschoi.timeisgold.feature.timeroutine.data.TimeRoutineFeatureStateIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.DayOfWeeksCheckStateIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.DayOfWeeksCheckStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager.DayOfWeeksPagerStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager.DayOfWeeksPagerStateIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineTitleIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineTitleStateHolder
import timber.log.Timber
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeRoutinePagerViewModel @Inject constructor(
    private val navigator: DestNavigatorPort,
    private val state: TimeRoutineFeatureState,

    private val routineTitleStateHolder: RoutineTitleStateHolder,
    private val routineDayOfWeeksStateHolder: DayOfWeeksCheckStateHolder,
    private val dayOfWeeksPagerStateHolder: DayOfWeeksPagerStateHolder,

    private val setRoutineUseCase: SetRoutineUseCase

) : ViewModel() {

    private val _intent = MutableSharedFlow<MetaEnvelope<TimeRoutinePagerUiIntent>>()

    private val currentRoutine = state.routine.map {
        it.asResultState()
    }.withResultStateLifecycle().onEach {
        Timber.d("watchCurrentRoutine - result=$it")
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    val uiState = combine(
        routineTitleStateHolder.state,
        routineDayOfWeeksStateHolder.state,
        dayOfWeeksPagerStateHolder.state
    ) { title, routineDayOfWeeks, dayOfWeeks ->
        TimeRoutinePagerUiState(
            dayOfWeekState = dayOfWeeks,
            titleState = title,
            routineDayOfWeeks = routineDayOfWeeks
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = TimeRoutinePagerUiState()
    )

    fun sendIntent(intent: TimeRoutinePagerUiIntent) {
        viewModelScope.launch {
            this@TimeRoutinePagerViewModel._intent.emit(MetaEnvelope(intent))
        }
    }

    init {
        watchIntent()

        watchRoutineEdit()

        watchDayOfWeekPager()

        watchCurrentDayOfWeek()
        watchCurrentRoutine()
        watchRoutineDayOfWeeks()
    }

    private fun watchDayOfWeekPager() {
        dayOfWeeksPagerStateHolder.currentDayOfWeek.onEach {
            state.reduce(
                TimeRoutineFeatureStateIntent.SelectDayOfWeek(it)
            )
        }.launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class)
    private fun watchRoutineEdit() {
        val editTitle = _intent.filter {
            it.payload is TimeRoutinePagerUiIntent.UpdateRoutineTitle
        }
        val editDayOfWeeks = _intent.filter {
            it.payload is TimeRoutinePagerUiIntent.CheckDayOfWeek
        }



        // TODO: jhchoi 2025. 11. 10.
        /*
        이게 문제인듯..
        상태에 의해서 apply가 실행된는데..
        상태를 명확히 한다는 장점은 있어도... apply는 사용자의 동작에 의해서만 되게 하는게 맞는것 같다.
        즉..인텐트만 수신하는 곳을 만들어야 하네..

        intent를 트리거로 해서 상태로부터 가져오는것보다..인텐트에서 직접 가져오는게 맞겠지..
        인텐트를 콤파인 해서 말이지..
        근데 intent가 stateHolder의 intent를 가진 구조라면..
        stateHolder의 인텐트 타입에 따라 분기 처리를 또해야 하는데..이게 맞는건가..
        ...
         */
        /*
        현재 요일도 있어야 하네... 요일을 키 삼아서 전달하니깐...
         */
        combine(
            routineTitleStateHolder.state.map { it.title },
            routineDayOfWeeksStateHolder.checkedDayOfWeeks,
            dayOfWeeksPagerStateHolder.currentDayOfWeek
        ) { title, dayOfWeeks: List<DayOfWeek>, currentDayOfWeek ->
            currentDayOfWeek to TimeRoutineVO(
                title = title,
                dayOfWeeks = dayOfWeeks.toSet()
            )
        }.distinctUntilChangedBy {
            it.second
        }.debounce(500).onEach {
            val dayOfWeek = it.first
            val routineVO = it.second
            setRoutineUseCase.invoke(
                routineVO = routineVO,
                dayOfWeek = dayOfWeek
            )
        }.launchIn(viewModelScope)
    }

    private fun watchCurrentDayOfWeek() {
        state.data.map {
            DayOfWeeksPagerStateIntent.Select(it.dayOfWeek)
        }.onEach {
            dayOfWeeksPagerStateHolder.reduce(it)
        }.launchIn(viewModelScope)
    }

    private fun watchRoutineDayOfWeeks() {
        val currentRoutine = currentRoutine.mapNotNull { (it as? ResultState.Success)?.data }
        combine(
            state.selectableDayOfWeeks,
            currentRoutine.map {
                it.payload.dayOfWeeks
            }
        ) { enableDayOfWeeks, routineDayOfWeeks ->
            DayOfWeeksCheckStateIntent.Update(
                enabled = enableDayOfWeeks,
                checked = routineDayOfWeeks.toSet()
            )
        }.onEach {
            routineDayOfWeeksStateHolder.sendIntent(it)
        }.launchIn(viewModelScope)
    }

    private fun watchCurrentRoutine() {
        val currentRoutine = currentRoutine.mapNotNull {
            (it as? ResultState.Success)?.data
        }
        currentRoutine.map {
            val title = it.payload.title
            RoutineTitleIntent.Update(title = title)
        }.onEach {
            Timber.d("watchCurrentRoutine - intent=$it")
            routineTitleStateHolder.sendIntent(it)
        }.launchIn(viewModelScope)
    }

    private fun watchIntent() = viewModelScope.launch {
        _intent.collect {
            handleIntentSideEffect(it.payload)
        }
    }

    private fun handleIntentSideEffect(intent: TimeRoutinePagerUiIntent) {
        when (intent) {

            is TimeRoutinePagerUiIntent.LoadRoutine -> {
                // TODO: jhchoi 2025. 11. 10. 이게 intent가 맞나..?
                dayOfWeeksPagerStateHolder.reduce(intent.stateIntent)
            }

            // TODO: jhchoi 2025. 11. 10.
            is TimeRoutinePagerUiIntent.CheckDayOfWeek -> {
                routineDayOfWeeksStateHolder.sendIntent(
                    intent.dayOfWeekCheckIntent
                )
            }

            is TimeRoutinePagerUiIntent.UpdateRoutineTitle -> {
                Timber.d("handleIntentSideEffect - UpdateRoutineTitle=$intent")
                routineTitleStateHolder.sendIntent(
                    RoutineTitleIntent.Update(intent.title)
                )
            }
        }
    }
}