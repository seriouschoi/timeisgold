package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
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
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchRoutineUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.data.TimeRoutineFeatureState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.DayOfWeeksCheckStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.pager.DayOfWeeksPagerStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineTitleStateHolder
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class TimeRoutinePagerViewModel @Inject constructor(
    private val navigator: DestNavigatorPort,
    private val featureState: TimeRoutineFeatureState,

    private val watchRoutineUseCase: WatchRoutineUseCase,
    private val routineTitleStateHolder: RoutineTitleStateHolder,
    private val routineDayOfWeeksStateHolder: DayOfWeeksCheckStateHolder,
    private val dayOfWeeksPagerStateHolder: DayOfWeeksPagerStateHolder,

    private val setRoutineUseCase: SetRoutineUseCase

) : ViewModel() {

    private val _intent = MutableSharedFlow<MetaEnvelope<TimeRoutinePagerUiIntent>>()

    // TODO: jhchoi 2025. 11. 17.  
    private val test = dayOfWeeksPagerStateHolder.state.map {
        it.currentDayOfWeek
    }.flatMapLatest {
        watchRoutineUseCase.invoke(it)
    }

    private val currentRoutine = featureState.routine.map {
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
        watchEditIntent()

        watchDayOfWeekPager()

        watchCurrentDayOfWeek()
        watchCurrentRoutine()
        watchRoutineDayOfWeeks()
    }

    private fun watchDayOfWeekPager() {
        dayOfWeeksPagerStateHolder.state.map {
            it.currentDayOfWeek
        }.onEach {
            featureState.selectDayOfWeek(it)
        }.launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class)
    private fun watchEditIntent() {
        combine(
            dayOfWeeksPagerStateHolder.state.map {
                it.currentDayOfWeek
            },
            routineTitleStateHolder.state.map {
                it.title
            },
            routineDayOfWeeksStateHolder.state.map {
                it.dayOfWeeksList
            }
        ) { currentDayOfWeek, title, dayOfWeeks ->
            TimeRoutineVO(
                title = title,
                dayOfWeeks = dayOfWeeks.filter {
                    it.enabled && it.checked
                }.map { it.dayOfWeek }.toSet()
            ) to currentDayOfWeek
        }.distinctUntilChangedBy {
            it.first
        }.debounce(500).onEach {
            setRoutineUseCase.invoke(it.first, it.second)
        }.launchIn(viewModelScope)
    }

    private fun watchCurrentDayOfWeek() {
        featureState.data.map {
            it.dayOfWeek
        }.onEach {
            dayOfWeeksPagerStateHolder.select(it)
        }.launchIn(viewModelScope)
    }

    private fun watchRoutineDayOfWeeks() {
        val currentRoutine = currentRoutine.mapNotNull { (it as? ResultState.Success)?.data }
        combine(
            featureState.selectableDayOfWeeks.mapNotNull {
                (it as? DomainResult.Success)?.value
            }.distinctUntilChanged(),
            currentRoutine.map {
                it.payload.dayOfWeeks
            }.distinctUntilChanged()
        ) { enableDayOfWeeks, routineDayOfWeeks ->
            Pair(enableDayOfWeeks, routineDayOfWeeks)
        }.onEach {
            routineDayOfWeeksStateHolder.update(
                enabled = it.first,
                checked = it.second
            )
        }.launchIn(viewModelScope)
    }

    private fun watchCurrentRoutine() {
        currentRoutine.mapNotNull {
            (it as? ResultState.Success)?.data?.payload?.title
        }.onEach {
            routineTitleStateHolder.updateTitle(it)
        }.launchIn(viewModelScope)
    }

    private fun watchIntent() = viewModelScope.launch {
        _intent.collect {
            handleIntentSideEffect(it.payload)
        }
    }

    private fun handleIntentSideEffect(intent: TimeRoutinePagerUiIntent) {
        when (intent) {
            is TimeRoutinePagerUiIntent.SelectDayOfWeek -> {
                dayOfWeeksPagerStateHolder.select(intent.dayOfWeek)
            }

            is TimeRoutinePagerUiIntent.CheckDayOfWeek -> {
                routineDayOfWeeksStateHolder.check(
                    dayOfWeeks = intent.checkedDayOfWeeks
                )
            }

            is TimeRoutinePagerUiIntent.UpdateRoutineTitle -> {
                Timber.d("handleIntentSideEffect - UpdateRoutineTitle=$intent")
                routineTitleStateHolder.updateTitle(intent.title)
            }
        }
    }
}