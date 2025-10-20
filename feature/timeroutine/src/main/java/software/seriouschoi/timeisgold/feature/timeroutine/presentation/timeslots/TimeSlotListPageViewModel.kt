package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

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
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainResult
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainSuccess
import software.seriouschoi.timeisgold.core.domain.mapper.toUiText
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchTimeRoutineCompositionUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.NormalizeMinutesForUiUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.valid.GetTimeSlotPolicyValidUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.edit.slot.TimeSlotEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotItemUiState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.asEntity
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.splitOverMidnight
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.logic.TimeSlotCalculator
import timber.log.Timber
import java.time.DayOfWeek
import javax.inject.Inject
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeSlotListPageViewModel @Inject constructor(
    private val navigator: DestNavigatorPort,
    private val watchTimeRoutineCompositionUseCase: WatchTimeRoutineCompositionUseCase,
    private val setTimeSlotsUseCase: SetTimeSlotListUseCase,
    private val normalizeMinutesForUiUseCase: NormalizeMinutesForUiUseCase,
    private val getTimeSlotPolicyValidUseCase: GetTimeSlotPolicyValidUseCase,
    private val timeSlotCalculator: TimeSlotCalculator
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
            started = SharingStarted.Lazily,
            initialValue = ResultState.Loading
        )

    private val routinePreUiStateFlow =
        routineCompositionFlow.onlyDomainResult().mapNotNull { routineComposition ->
            UiPreState.Routine(
                routineDomainResult = routineComposition
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )


    private val _intent = MutableSharedFlow<Envelope<TimeRoutinePageUiIntent>>()

    private val _timeSlotUpdatePreUiStateFlow = MutableSharedFlow<UiPreState.UpdateSlotList>()

    val uiState: StateFlow<TimeSlotListPageUiState> = merge(
        routinePreUiStateFlow.mapNotNull { it },
        _timeSlotUpdatePreUiStateFlow
    ).scan(
        TimeSlotListPageUiState().loadingState()
    ) { acc: TimeSlotListPageUiState, value: UiPreState ->
        acc.reduce(value)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TimeSlotListPageUiState().loadingState()
    )
    private val _uiEvent: MutableSharedFlow<Envelope<TimeSlotListPageUiEvent>> = MutableSharedFlow()
    val uiEvent: SharedFlow<Envelope<TimeSlotListPageUiEvent>> = _uiEvent

    init {
        watchIntent()
    }

    fun load(dayOfWeek: DayOfWeek) {
        viewModelScope.launch {
            dayOfWeekFlow.emit(dayOfWeek)
        }
    }

    private fun watchIntent() {
        _intent.onEach {
            handleIntentSideEffect(it.payload)
        }.launchIn(viewModelScope)
    }


    private suspend fun handleIntentSideEffect(intent: TimeRoutinePageUiIntent) {
        when (intent) {
            is TimeRoutinePageUiIntent.ShowSlotEdit -> {
                val route = TimeSlotEditScreenRoute(
                    timeSlotUuid = intent.slotId,
                    timeRoutineUuid = intent.routineId
                )
                navigator.navigate(route)
            }

            is TimeRoutinePageUiIntent.UpdateTimeSlotList -> {
                updateTimeSlotList()
            }

            is TimeRoutinePageUiIntent.UpdateTimeSlotUi -> {
                handleUpdateTimeSlot(intent)
            }
        }
    }

    private var dragMinsAcc = 0
    private suspend fun handleUpdateTimeSlot(
        intent: TimeRoutinePageUiIntent.UpdateTimeSlotUi,
    ) {
        val (newList, nextAcc) = timeSlotCalculator.adjustSlotList(
            intent = intent,
            currentList = uiState.value.slotItemList,
            dragAcc = dragMinsAcc
        )
        dragMinsAcc = nextAcc
        _timeSlotUpdatePreUiStateFlow.emit(
            UiPreState.UpdateSlotList(newList)
        )
    }

    private fun updateTimeSlotList() {
        flowResultState {
            val currentRoutine =
                routineCompositionFlow.onlyDomainSuccess().first()
                    ?: return@flowResultState DomainResult.Failure(DomainError.NotFound.TimeRoutine)

            val dataState = uiState.first()

            val updateSlots = dataState.slotItemList.map {
                it.asEntity()
            }

            setTimeSlotsUseCase.invoke(
                timeRoutineUuid = currentRoutine.timeRoutine.uuid,
                timeSlotList = updateSlots
            )
        }.onlyDomainResult().onEach { domainResult ->
            when (domainResult) {
                is DomainResult.Failure -> {
                    TimeSlotListPageUiEvent.ShowToast(
                        domainResult.error.toUiText(),
                        Toast.LENGTH_SHORT
                    )
                }

                is DomainResult.Success -> {
                    TimeSlotListPageUiEvent.ShowToast(
                        UiText.MultipleResArgs.create(
                            CommonR.string.message_format_complete,
                            CommonR.string.text_save,
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

private fun TimeSlotListPageUiState.reduce(value: UiPreState): TimeSlotListPageUiState {
    return when (value) {
        is UiPreState.Routine -> {
            this.reduceFromRoutine(value)
        }

        is UiPreState.UpdateSlotList -> {
            this.copy(slotItemList = value.timeSlotList)
        }
    }
}


private fun TimeSlotListPageUiState.reduceFromRoutine(value: UiPreState.Routine): TimeSlotListPageUiState {
    val domainResult = value.routineDomainResult
    return when (domainResult) {
        is DomainResult.Failure -> {
            val newState = this.copy(
                loadingMessage = null,
            )
            when (domainResult.error) {
                is DomainError.NotFound -> {
                    //빈 슬롯 리턴.
                    newState.copy(
                        errorState = null,
                        slotItemList = emptyList()
                    )
                }

                else -> {
                    val errorState = TimeSlotListPageErrorState(
                        errorMessage = domainResult.error.toUiText(),
                    )
                    newState.copy(
                        errorState = errorState
                    )
                }
            }
        }

        is DomainResult.Success -> {
            val newState = this.copy(
                loadingMessage = null,
                errorState = null
            )
            val routineComposition = domainResult.value
            val routineUuid = routineComposition.timeRoutine.uuid
            newState.copy(
                slotItemList = routineComposition.timeSlots.flatMap { slotEntity: TimeSlotEntity ->
                    val slotItem = slotEntity.toSlotItem(routineUuid)
                    slotItem.splitOverMidnight()
                },
            )
        }

        null -> {
            this.copy(
                loadingMessage = UiText.MultipleResArgs.create(
                    CommonR.string.message_format_loading,
                    CommonR.string.text_routine
                )
            )
        }
    }
}


private fun TimeSlotEntity.toSlotItem(routineUuid: String): TimeSlotItemUiState {
    return TimeSlotItemUiState(
        slotUuid = this.uuid,
        routineUuid = routineUuid,
        title = this.title,
        startMinutesOfDay = this.startTime.asMinutes(),
        endMinutesOfDay = this.endTime.asMinutes(),
        isSelected = false,
    )
}

private sealed interface UiPreState {
    data class Routine(
        val routineDomainResult: DomainResult<TimeRoutineComposition>?,
    ) : UiPreState

    data class UpdateSlotList(
        val timeSlotList: List<TimeSlotItemUiState>
    ) : UiPreState
}

