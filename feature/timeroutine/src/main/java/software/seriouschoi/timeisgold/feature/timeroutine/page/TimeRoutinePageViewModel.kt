package software.seriouschoi.timeisgold.feature.timeroutine.page

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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
import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.core.common.util.asFormattedString
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainResult
import software.seriouschoi.timeisgold.core.domain.mapper.onlySuccess
import software.seriouschoi.timeisgold.core.domain.mapper.toUiText
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchTimeRoutineCompositionUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.GetTimeSlotValidUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit.TimeSlotEditScreenRoute
import timber.log.Timber
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
    private val setTimeSlotUseCase: SetTimeSlotUseCase,
    private val getTimeSlotValidUseCase: GetTimeSlotValidUseCase,
) : ViewModel() {

    private val dayOfWeekFlow = MutableStateFlow<DayOfWeek?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val routineCompositionFlow: StateFlow<ResultState<DomainResult<TimeRoutineComposition>>> =
        dayOfWeekFlow.mapNotNull { it }.flatMapConcat {
            watchTimeRoutineCompositionUseCase.invoke(it)
        }.onEach {
            Timber.d("received routine composition.")
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
    private val _uiEvent: MutableSharedFlow<Envelope<TimeRoutinePageUiEvent>> = MutableSharedFlow()
    val uiEvent: SharedFlow<Envelope<TimeRoutinePageUiEvent>> = _uiEvent

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
                if (!intent.onlyUi) {
                    updateTimeSlot(intent)
                }
            }
        }
    }

    private fun updateTimeSlot(intent: TimeRoutinePageUiIntent.UpdateSlot) {
        flowResultState {
            val currentRoutine =
                routineCompositionFlow.onlySuccess().first()
                    ?: return@flowResultState DomainResult.Failure(DomainError.NotFound.TimeRoutine)

            val timeSlot =
                currentRoutine.timeSlots.find { it.uuid == intent.uuid }
                    ?: return@flowResultState DomainResult.Failure(DomainError.NotFound.TimeSlot)

            val newTimeSlot = timeSlot.copy(
                startTime = intent.newStart,
                endTime = intent.newEnd
            )

            setTimeSlotUseCase.invoke(
                timeRoutineUuid = currentRoutine.timeRoutine.uuid,
                timeSlotData = newTimeSlot
            )
        }.onlyDomainResult().onEach { domainResult ->
            when (domainResult) {
                is DomainResult.Failure -> {
                    TimeRoutinePageUiEvent.ShowToast(
                        domainResult.error.toUiText(),
                        Toast.LENGTH_SHORT
                    )
                }

                is DomainResult.Success -> {
                    val startTimeText = intent.newStart.asFormattedString()
                    val endTimeText = intent.newEnd.asFormattedString()
                    TimeRoutinePageUiEvent.ShowToast(
                        UiText.Res.create(
                            CommonR.string.message_format_changed,
                            "$startTimeText-$endTimeText"
                        ),
                        Toast.LENGTH_SHORT
                    )
                }

                null -> {
                    //no work
                    null
                }
            }?.let {
                _uiEvent.emit(Envelope(it))
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

        val isOverlap = intent.isOverlap(routineState.slotItemList)
        if (!isOverlap) {
            val newSlotItemList = routineState.slotItemList.map {
                if (it.slotUuid == intent.uuid) {
                    it.copy(
                        startMinutesOfDay = intent.newStart.asMinutes(),
                        endMinutesOfDay = intent.newEnd.asMinutes(),
                        startMinuteText = intent.newStart.asFormattedString(),
                        endMinuteText = intent.newEnd.asFormattedString(),
                        isSelected = intent.onlyUi
                    ).splitOverMidnight()
                } else {
                    listOf(it)
                }
            }.flatten().distinct()

            routineState.copy(
                slotItemList = newSlotItemList
            )
        } else this
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
            val routineUuid = routineComposition.timeRoutine.uuid
            routineState.copy(
                title = routineComposition.timeRoutine.title,
                slotItemList = routineComposition.timeSlots.map { slotEntity: TimeSlotEntity ->
                    val slotItem = slotEntity.toSlotItem(routineUuid)
                    slotItem.splitOverMidnight()
                }.flatten(),
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

private fun TimeSlotCardUiState.splitOverMidnight(): List<TimeSlotCardUiState> {
    return if (this.startMinutesOfDay > this.endMinutesOfDay) {
        //ex: 22 ~ 6

        //24 + 6(endTime)
        val overEndMinutes = LocalTimeUtil.DAY_MINUTES + (this.endMinutesOfDay)
        //00 - (24 - 22(startTime))
        val negativeStartMinutes =
            0 - (LocalTimeUtil.DAY_MINUTES - this.startMinutesOfDay)
        listOf(
            this.copy(
                startMinutesOfDay = this.startMinutesOfDay,
                endMinutesOfDay = overEndMinutes,
            ),
            this.copy(
                startMinutesOfDay = negativeStartMinutes,
                endMinutesOfDay = this.endMinutesOfDay,
            )
        )
    } else {
        listOf(
            this
        )
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

private fun TimeSlotEntity.toSlotItem(routineUuid: String): TimeSlotCardUiState {
    return TimeSlotCardUiState(
        slotUuid = this.uuid,
        routineUuid = routineUuid,
        title = this.title,
        startMinutesOfDay = this.startTime.asMinutes(),
        endMinutesOfDay = this.endTime.asMinutes(),
        startMinuteText = this.startTime.asFormattedString(),
        endMinuteText = this.endTime.asFormattedString(),
        slotClickIntent = TimeRoutinePageUiIntent.ShowSlotEdit(
            this.uuid, routineUuid
        ),
        isSelected = false,
    )
}

private fun TimeRoutinePageUiIntent.UpdateSlot.isOverlap(slotItemList: List<TimeSlotCardUiState>) =
    slotItemList.any {
        if (it.slotUuid == this.uuid) false
        else {
            val intentRanges = if (this.newStart > this.newEnd) {
                listOf(
                    0 until this.newEnd.asMinutes(),
                    this.newStart.asMinutes() until LocalTimeUtil.DAY_MINUTES
                )
            } else {
                listOf(
                    this.newStart.asMinutes() until this.newEnd.asMinutes()
                )
            }
            intentRanges.any { intentRange ->
                if (intentRange.first > intentRange.last) {
                    0 until intentRange.last
                    intentRange.first until LocalTimeUtil.DAY_MINUTES
                    false
                } else {
                    val slotRange = it.let {
                        it.startMinutesOfDay until it.endMinutesOfDay
                    }
                    intentRange.any {
                        slotRange.contains(it)
                    }
                }
            }
        }
    }