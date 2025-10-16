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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainSuccess
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetRoutineDayOfWeeksUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetRoutineTitleUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.data.TimeRoutineFeatureState
import software.seriouschoi.timeisgold.feature.timeroutine.data.TimeRoutineFeatureStateIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.DayOfWeeksCheckIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.components.dayofweeks.check.DayOfWeeksCheckStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit.routine.TimeRoutineEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit.slot.TimeSlotEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.DayOfWeeksPagerStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder.DayOfWeeksPagerStateIntent
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

    private val setRoutineTitleUseCase: SetRoutineTitleUseCase,
    private val setRoutineDayOfWeeksUseCase: SetRoutineDayOfWeeksUseCase,
) : ViewModel() {

    private val _intent = MutableSharedFlow<Envelope<TimeRoutinePagerUiIntent>>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentRoutine =
        state.routineDefinition.asResultState().onlyDomainSuccess().stateIn(
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

    private fun selectDayOfWeek(dayOfWeek: DayOfWeek) {
        viewModelScope.launch {
            state.reduce(
                TimeRoutineFeatureStateIntent.SelectDayOfWeek(dayOfWeek)
            )
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
        watchTitleInput()
        watchDayOfWeekCheck()

        watchCurrentDayOfWeek()

        watchCurrentRoutine()
        watchRoutineDayOfWeeks()

    }

    @OptIn(FlowPreview::class)
    private fun watchDayOfWeekCheck() {
        val checkedDayOfWeeks =
            routineDayOfWeeksStateHolder.checkedDayOfWeeks.debounce(500).distinctUntilChanged()

        val dayOfWeek = dayOfWeeksPagerStateHolder.currentDayOfWeek

        combine(checkedDayOfWeeks, dayOfWeek) { checkedDayOfWeeks, dayOfWeek ->
            checkedDayOfWeeks to dayOfWeek
        }.distinctUntilChangedBy {
            it.first
        }.onEach {
            Timber.d("watchDayOfWeekCheck - checkedDayOfWeeks=${it.first}, dayOfWeek=${it.second}")
            setRoutineDayOfWeeksUseCase.invoke(
                dayOfWeeks = it.first,
                currentDayOfWeek = it.second
            )
        }.launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun watchTitleInput() {

        val inputTitle = _intent.mapNotNull {
            (it.payload as? TimeRoutinePagerUiIntent.UpdateRoutineTitle)?.title
        }.debounce(500).distinctUntilChanged()

        val dayOfWeek = dayOfWeeksPagerStateHolder.currentDayOfWeek

        combine(
            inputTitle,
            dayOfWeek,
        ) { title: String, dayOfWeek: DayOfWeek ->
            title to dayOfWeek
        }.distinctUntilChangedBy {
            it.first //title 변경시에만 실행.
        }.onEach {
            val title = it.first
            val dayOfWeek = it.second
            Timber.d("watchUpdateTitle - title=$title, dayOfWeek=$dayOfWeek")
            setRoutineTitleUseCase.invoke(
                title = title,
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
        combine(state.selectableDayOfWeeks, currentRoutine) { enableDayOfWeeks, routine ->
            val currentRoutineDayOfWeeks = routine?.dayOfWeeks?.map { it.dayOfWeek } ?: emptyList()
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
            val title = it?.timeRoutine?.title ?: ""
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
}