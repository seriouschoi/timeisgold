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
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.DayOfWeeksCheckIntent
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
    }.withResultStateLifecycle().mapNotNull {
        (it as? ResultState.Success)?.data
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
        }.launchIn(
            viewModelScope
        )
    }

    private fun watchCurrentDayOfWeek() {
        state.data.map {
            DayOfWeeksPagerStateIntent.Select(it.dayOfWeek)
        }.onEach {
            dayOfWeeksPagerStateHolder.reduce(it)
        }.launchIn(viewModelScope)
    }

    private fun watchRoutineDayOfWeeks() {
        combine(
            state.selectableDayOfWeeks,
            currentRoutine.map {
                it?.payload?.dayOfWeeks
            }
        ) { enableDayOfWeeks, routineDayOfWeeks ->
            val currentRoutineDayOfWeeks = routineDayOfWeeks ?: emptySet()
            Timber.d("watchRoutineDayOfWeeks - enableDayOfWeeks=$enableDayOfWeeks, currentRoutineDayOfWeeks=$currentRoutineDayOfWeeks")
            DayOfWeeksCheckIntent.Update(
                checked = currentRoutineDayOfWeeks,
                enabled = enableDayOfWeeks
            )
        }.onEach {
            routineDayOfWeeksStateHolder.reduce(it)
        }.launchIn(viewModelScope)
    }

    private fun watchCurrentRoutine() {
        currentRoutine.map {
            val title = it?.payload?.title ?: ""
            RoutineTitleIntent.Update(title)
        }.onEach {
            Timber.d("watchCurrentRoutine - intent=$it")
            routineTitleStateHolder.reduce(it)
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
                dayOfWeeksPagerStateHolder.reduce(intent.stateIntent)
            }

            is TimeRoutinePagerUiIntent.CheckDayOfWeek -> {
                routineDayOfWeeksStateHolder.reduce(
                    intent.dayOfWeekCheckIntent
                )
            }

            is TimeRoutinePagerUiIntent.UpdateRoutineTitle -> {
                Timber.d("handleIntentSideEffect - UpdateRoutineTitle=$intent")
                routineTitleStateHolder.reduce(
                    RoutineTitleIntent.Update(intent.title)
                )
            }
        }
    }
}