package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
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
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotListUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotListStateIntent
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.asEntity
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.splitOverMidnight
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.logic.TimeSlotCalculator
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditState
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateHolder
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit.TimeSlotEditStateIntent
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
    private val setTimeSlotUseCase: SetTimeSlotUseCase,

    private val timeSlotListStateHolder: TimeSlotListStateHolder,
    private val timeSlotEditStateHolder: TimeSlotEditStateHolder,
    private val timeSlotCalculator: TimeSlotCalculator,
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

    private val _intent = MutableSharedFlow<Envelope<TimeSlotListPageUiIntent>>()

    val uiState = combine(
        timeSlotListStateHolder.state,
        timeSlotEditStateHolder.state
    ) { listState, editState ->
        TimeSlotListPageUiState(
            slotListState = listState,
            editState = editState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TimeSlotListPageUiState()
    )

    private val _uiEvent: MutableSharedFlow<Envelope<TimeSlotListPageUiEvent>> = MutableSharedFlow()
    val uiEvent: SharedFlow<Envelope<TimeSlotListPageUiEvent>> = _uiEvent

    init {
        watchIntent()
        watchRoutineComposition()
        watchTimeSlotEdit()
    }

    @OptIn(FlowPreview::class)
    private fun watchTimeSlotEdit() {
        timeSlotEditStateHolder.state.mapNotNull { it }.debounce(
            500
        ).onEach {
            updateTimeSlotEdit(it)
        }.launchIn(viewModelScope)
    }

    fun load(dayOfWeek: DayOfWeek) {
        viewModelScope.launch {
            dayOfWeekFlow.emit(dayOfWeek)
        }
    }

    private fun watchRoutineComposition() {
        routineCompositionFlow.map { resultState: ResultState<DomainResult<TimeRoutineComposition>> ->
            when (resultState) {
                is ResultState.Success -> {
                    when (val domainResult = resultState.data) {
                        is DomainResult.Failure -> {
                            when (domainResult.error) {
                                is DomainError.NotFound -> {
                                    TimeSlotListStateIntent.UpdateList(emptyList())
                                }

                                else -> {
                                    val errorMessage = domainResult.error.toUiText()
                                    TimeSlotListStateIntent.Error(errorMessage)
                                }
                            }
                        }

                        is DomainResult.Success -> {
                            val routineComposition = domainResult.value
                            val routineUuid = routineComposition.timeRoutine.uuid
                            val slotItemList = routineComposition.timeSlots.flatMap { slot ->
                                slot.toSlotItem(
                                    routineUuid = routineUuid
                                ).splitOverMidnight()
                            }
                            TimeSlotListStateIntent.UpdateList(slotItemList)
                        }
                    }
                }

                is ResultState.Error -> TimeSlotListStateIntent.Error(
                    UiText.Res(CommonR.string.message_error_tech_unknown)
                )

                ResultState.Loading -> TimeSlotListStateIntent.Loading
            }
        }.onEach {
            timeSlotListStateHolder.sendIntent(it)
        }.launchIn(viewModelScope)
    }

    private fun watchIntent() {
        _intent.onEach {
            handleIntentSideEffect(it.payload)
        }.launchIn(viewModelScope)
    }

    private suspend fun handleIntentSideEffect(intent: TimeSlotListPageUiIntent) {
        when (intent) {

            is TimeSlotListPageUiIntent.UpdateTimeSlotList -> {
                updateTimeSlotList()
            }

            is TimeSlotListPageUiIntent.UpdateTimeSlotUi -> {
                handleUpdateTimeSlot(intent)
            }

            TimeSlotListPageUiIntent.Cancel -> {
                timeSlotEditStateHolder.sendIntent(TimeSlotEditStateIntent.Clear)
            }

            is TimeSlotListPageUiIntent.UpdateTimeSlotEdit -> {
                timeSlotEditStateHolder.sendIntent(
                    intent.slotEditState
                )
            }
        }
    }


    private var dragMinsAcc = 0

    private suspend fun handleUpdateTimeSlot(
        intent: TimeSlotListPageUiIntent.UpdateTimeSlotUi,
    ) {
        val slotListState = timeSlotListStateHolder.state.first()
        val (newList, nextAcc) = timeSlotCalculator.adjustSlotList(
            intent = intent,
            currentList = slotListState.slotItemList,
            dragAcc = dragMinsAcc
        )
        dragMinsAcc = nextAcc
        timeSlotListStateHolder.sendIntent(
            TimeSlotListStateIntent.UpdateList(newList)
        )
    }

    private fun updateTimeSlotEdit(state: TimeSlotEditState) {
        flowResultState {
            val currentDayOfWeek = dayOfWeekFlow.first()
                ?: return@flowResultState DomainResult.Failure(DomainError.Validation.NoSelectedDayOfWeek)



        }
        TODO("Not yet implemented")
    }

    private fun updateTimeSlotList() {
        flowResultState {
            val currentRoutine =
                routineCompositionFlow.onlyDomainSuccess().first()
                    ?: return@flowResultState DomainResult.Failure(DomainError.NotFound.TimeRoutine)

            val dataState = timeSlotListStateHolder.state.first()
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

    fun sendIntent(createRoutine: TimeSlotListPageUiIntent) {
        viewModelScope.launch {
            _intent.emit(Envelope(createRoutine))
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
