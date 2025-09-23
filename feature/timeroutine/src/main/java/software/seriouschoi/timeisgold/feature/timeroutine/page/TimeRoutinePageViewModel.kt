package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.common.ui.flowResultState
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainResult
import software.seriouschoi.timeisgold.core.domain.mapper.onlySuccess
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchTimeRoutineCompositionUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.WatchTimeSlotUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit.TimeSlotEditScreenRoute
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeRoutinePageViewModel @Inject constructor(
    private val watchTimeRoutineCompositionUseCase: WatchTimeRoutineCompositionUseCase,
    private val navigator: DestNavigatorPort,
    private val savedStateHandle: SavedStateHandle,
    private val setTimeSlotUseCase: SetTimeSlotUseCase,
    private val watchTimeSlotUseCase: WatchTimeSlotUseCase,
) : ViewModel() {

    private val dayOfWeekFlow = MutableStateFlow<DayOfWeek?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val routineCompositionFlow: StateFlow<ResultState<DomainResult<TimeRoutineComposition>>> =
        dayOfWeekFlow.mapNotNull { it }.flatMapConcat {
            watchTimeRoutineCompositionUseCase.invoke(it)
        }.asResultState().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ResultState.Loading
        )

    private val routinePreUiStateFlow = combine(
        dayOfWeekFlow.mapNotNull { it },
        routineCompositionFlow.onlyDomainResult().mapNotNull { it }
    ) { dayOfWeek, routineComposition ->
        UiPreState.Routine(
            currentDayOfWeek = dayOfWeek,
            routineDomainResult = routineComposition
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    private val _intent = MutableSharedFlow<Envelope<TimeRoutinePageUiIntent>>()


    val uiState: StateFlow<TimeRoutinePageUiState> = merge(
        routinePreUiStateFlow.mapNotNull { it },
        _intent.mapNotNull {
            UiPreState.Intent(it.payload)
        }
    ).scan(
        TimeRoutinePageUiState.Loading.default()
    ) { acc: TimeRoutinePageUiState, value: UiPreState ->
        acc.reduce(value)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TimeRoutinePageUiState.Loading.default()
    )

    init {
        viewModelScope.launch {
            _intent.collect {
                handleIntentSideEffect(it.payload)
            }
        }
    }

    fun load(dayOfWeek: DayOfWeek) {
        viewModelScope.launch {
            dayOfWeekFlow.emit(dayOfWeek)
        }
    }


    private fun handleIntentSideEffect(intent: TimeRoutinePageUiIntent) {
        when (intent) {
            is TimeRoutinePageUiIntent.ModifyRoutine,
            is TimeRoutinePageUiIntent.CreateRoutine,
                -> {
                val dayOfWeekOrdinal = dayOfWeekFlow.value?.ordinal
                if (dayOfWeekOrdinal != null)
                    navigator.navigate(TimeRoutineEditScreenRoute(dayOfWeekOrdinal))
            }

            is TimeRoutinePageUiIntent.ShowSlotEdit -> {
                val route = TimeSlotEditScreenRoute(
                    timeSlotUuid = intent.slotId,
                    timeRoutineUuid = intent.routineId
                )
                navigator.navigate(route)
            }

            is TimeRoutinePageUiIntent.UpdateSlot -> {
                updateTimeSlot(intent)
            }
        }
    }

    private fun updateTimeSlot(intent: TimeRoutinePageUiIntent.UpdateSlot) {
        flowResultState {
            val currentRoutine =
                routineCompositionFlow.onlySuccess().first() ?: return@flowResultState

            val timeSlot =
                currentRoutine.timeSlots.find { it.uuid == intent.uuid } ?: return@flowResultState

            val newTimeSlot = timeSlot.copy(
                startTime = intent.newStart,
                endTime = intent.newEnd
            )

            setTimeSlotUseCase.invoke(
                timeRoutineUuid = currentRoutine.timeRoutine.uuid,
                timeSlotData = newTimeSlot
            )

        }.onEach { state: ResultState<Unit> ->
            when (state) {
                is ResultState.Error -> {
                    // TODO: jhchoi 2025. 9. 23. show error.
                }

                ResultState.Loading -> {
                    //no working.
                }

                is ResultState.Success -> {
                    //no working.
                }
            }
        }.launchIn(viewModelScope)
    }

    fun sendIntent(createRoutine: TimeRoutinePageUiIntent) {
        viewModelScope.launch {
            _intent.emit(Envelope(createRoutine))
        }
    }
}

private fun TimeRoutinePageUiState.reduce(value: UiPreState): TimeRoutinePageUiState {
    return when (value) {
        is UiPreState.Intent -> {
            this.reduce(value)
        }

        is UiPreState.Routine -> {
            this.reduce(value)
        }
    }
}

private fun TimeRoutinePageUiState.reduce(
    value: UiPreState.Intent,
): TimeRoutinePageUiState = when (val intent = value.intent) {
    is TimeRoutinePageUiIntent.UpdateSlot -> {
        val routineState = this as? TimeRoutinePageUiState.Routine
            ?: TimeRoutinePageUiState.Routine.default()

        val newSlotItemList = routineState.slotItemList.map {
            if (it.uuid == intent.uuid) {
                it.copy(
                    startTime = intent.newStart,
                    endTime = intent.newEnd
                )
            } else {
                it
            }
        }
        routineState.copy(
            slotItemList = newSlotItemList
        )
    }

    else -> this
}

private fun TimeRoutinePageUiState.reduce(value: UiPreState.Routine): TimeRoutinePageUiState {
    val domainResult = value.routineDomainResult
    return when (domainResult) {
        is DomainResult.Failure -> {
            TimeRoutinePageUiState.Error(
                errorMessage = UiText.Res.create(
                    CommonR.string.message_format_routine_create_confirm,
                    value.currentDayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                ),
                confirmButton = TimeRoutinePageButtonState(
                    buttonLabel = UiText.Res.create(CommonR.string.text_confirm),
                    intent = TimeRoutinePageUiIntent.ModifyRoutine
                )
            )
        }

        is DomainResult.Success -> {
            val routineState =
                this as? TimeRoutinePageUiState.Routine ?: TimeRoutinePageUiState.Routine.default()
            val routineComposition = domainResult.value
            routineState.copy(
                title = routineComposition.timeRoutine.title,
                slotItemList = routineComposition.timeSlots.map {
                    TimeSlotCardUiState(
                        uuid = it.uuid,
                        title = it.title,
                        startTime = it.startTime,
                        endTime = it.endTime,
                        slotClickIntent = TimeRoutinePageUiIntent.ShowSlotEdit(
                            it.uuid, routineComposition.timeRoutine.uuid
                        )
                    )
                },
                dayOfWeeks = routineComposition.dayOfWeeks.map {
                    it.dayOfWeek
                }.sorted(),
                dayOfWeekName = value.currentDayOfWeek.getDisplayName(
                    TextStyle.SHORT,
                    Locale.getDefault()
                )
            )
        }

        null -> {
            TimeRoutinePageUiState.Loading.default()
        }
    }
}


private sealed interface UiPreState {
    data class Routine(
        val currentDayOfWeek: DayOfWeek,
        val routineDomainResult: DomainResult<TimeRoutineComposition>?,
    ) : UiPreState

    data class Intent(
        val intent: TimeRoutinePageUiIntent,
    ) : UiPreState
}
