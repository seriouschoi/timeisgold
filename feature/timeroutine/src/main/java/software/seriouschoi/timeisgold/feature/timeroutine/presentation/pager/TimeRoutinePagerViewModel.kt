package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainSuccess
import software.seriouschoi.timeisgold.core.domain.mapper.onlySuccess
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchAllRoutineDayOfWeeksUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchTimeRoutineDefinitionUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.data.TimeRoutineFeatureState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit.routine.TimeRoutineEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit.slot.TimeSlotEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.DayOfWeeksPagerStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.DayOfWeeksPagerStateIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineDayOfWeeksIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineDayOfWeeksStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineTitleIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.RoutineTitleStateHolder
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
    private val routineDayOfWeeksStateHolder: RoutineDayOfWeeksStateHolder,
    private val dayOfWeeksPagerStateHolder: DayOfWeeksPagerStateHolder,

    private val allDayOfWeeksUseCase: WatchAllRoutineDayOfWeeksUseCase,
    private val watchTimeRoutineDefinitionUseCase: WatchTimeRoutineDefinitionUseCase
) : ViewModel() {

    private val _intent = MutableSharedFlow<Envelope<TimeRoutinePagerUiIntent>>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentRoutine = state.data.map { it.dayOfWeek }
        .flatMapLatest {
            watchTimeRoutineDefinitionUseCase.invoke(it)
        }.asResultState().onlyDomainSuccess().stateIn(
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
            this@TimeRoutinePagerViewModel._intent.emit(Envelope(intent))
        }
    }

    private fun handleIntentSideEffect(intent: TimeRoutinePagerUiIntent) {
        when (intent) {
            TimeRoutinePagerUiIntent.ModifyRoutine -> {
                moveToRoutineEdit()
            }

            is TimeRoutinePagerUiIntent.AddRoutine -> {
                moveToTimeSlotEdit()
            }

            is TimeRoutinePagerUiIntent.LoadRoutine -> {
                selectDayOfWeek(intent.dayOfWeek)
            }

            else -> {
                //no work.
            }
        }
    }

    private fun selectDayOfWeek(dayOfWeek: DayOfWeek) {
        viewModelScope.launch {
            val newState = state.data.value.copy(
                dayOfWeek = dayOfWeek
            )
            state.data.emit(newState)
        }
    }

    private fun moveToTimeSlotEdit() {
        viewModelScope.launch {
            val routineDefinition = currentRoutine.first() ?: return@launch
            val routineUuid = routineDefinition.timeRoutine.uuid

            navigator.navigate(
                TimeSlotEditScreenRoute(
                    timeRoutineUuid = routineUuid,
                    timeSlotUuid = null,
                )
            )
        }
    }

    private fun moveToRoutineEdit() {
        viewModelScope.launch {
            val currentDayOfWeek = state.data.first().dayOfWeek

            val route = TimeRoutineEditScreenRoute(
                dayOfWeekOrdinal = currentDayOfWeek.ordinal
            )
            navigator.navigate(route)
        }
    }

    init {
        watchIntent()
        watchDayOfWeeksPager()

        watchCurrentRoutine()
        watchRoutineDayOfWeeks()
    }

    private fun watchDayOfWeeksPager() {
        state.data.map {
            DayOfWeeksPagerStateIntent.Select(it.dayOfWeek)
        }.onEach {
            dayOfWeeksPagerStateHolder.reduce(it)
        }.launchIn(viewModelScope)
    }

    private fun watchRoutineDayOfWeeks() {
        combine(allDayOfWeeksUseCase.invoke(), currentRoutine) { allDayOfWeeks, routine ->
            val allRoutinesDayOfWeeks = allDayOfWeeks.onlySuccess() ?: emptyList()
            val currentRoutineDayOfWeeks = routine?.dayOfWeeks?.map { it.dayOfWeek } ?: emptyList()

            val enableDayOfWeeks = DayOfWeek.entries.filter { day ->
                val usedByOtherRoutine = allRoutinesDayOfWeeks.contains(day)
                val usedByCurrentRoutine = currentRoutineDayOfWeeks.contains(day)

                // 현재 루틴에서 사용 중이거나, 다른 루틴에서 사용 중이 아닌 경우만 활성
                !usedByOtherRoutine || usedByCurrentRoutine
            }
            RoutineDayOfWeeksIntent.Update(
                checked = currentRoutineDayOfWeeks,
                enabled = enableDayOfWeeks
            )
        }.onEach {
            routineDayOfWeeksStateHolder.reduce(it)
        }.launchIn(viewModelScope)
    }

    private fun watchCurrentRoutine() {
        currentRoutine.map {
            val title = it?.timeRoutine?.title ?: ""
            RoutineTitleIntent.Update(title)
        }.onEach {
            routineTitleStateHolder.update(it)
        }.launchIn(viewModelScope)
    }

    private fun watchIntent() = viewModelScope.launch {
        _intent.collect {
            handleIntentSideEffect(it.payload)
        }
    }
}