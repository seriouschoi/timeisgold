package software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.common.ui.flowResultState
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainResult
import software.seriouschoi.timeisgold.core.domain.mapper.toUiText
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.usecase.timeslot.DeleteTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.GetTimeSlotValidUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.WatchTimeSlotUseCase
import java.time.LocalTime
import javax.inject.Inject
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

@HiltViewModel
internal class TimeSlotEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val watchTimeSlotUseCase: WatchTimeSlotUseCase,
    private val navigator: DestNavigatorPort,
    private val saveTimeSlotUseCase: SetTimeSlotUseCase,
    private val deleteTimeSlotUseCase: DeleteTimeSlotUseCase,
    private val getTimeSlotValidUseCase: GetTimeSlotValidUseCase,
) : ViewModel() {
    private val currentRoute get() = savedStateHandle.toRoute<TimeSlotEditScreenRoute>()
    private val intentFlow = MutableSharedFlow<Envelope<TimeSlotEditIntent>>()

    private val initFlow = flow {
        val slotUuid = currentRoute.timeSlotUuid
        if (slotUuid != null) {
            val domainResult = watchTimeSlotUseCase.invoke(
                slotUuid
            ).first()
            emit(domainResult)
        } else {
            emit(DomainResult.Failure(DomainError.NotFound.TimeSlot))
        }
    }.asResultState().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        ResultState.Loading
    )

    private val _loadingStateFlow = MutableSharedFlow<Boolean>()

    val uiState: StateFlow<TimeSlotEditUiState> = merge(
        initFlow.onlyDomainResult()
            .mapNotNull { domainResult: DomainResult<TimeSlotEntity>? ->
                domainResult?.let { UiPreState.Init(it) }
            },
        intentFlow.mapNotNull {
            UiPreState.Intent(it.payload)
        },
        _loadingStateFlow.mapNotNull {
            UiPreState.Loading(it)
        }
    ).scan(TimeSlotEditUiState()) { currentState, preState: UiPreState ->
        preState.applyTo(currentState)
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        TimeSlotEditUiState()
    )

    private val currentTimeSlotFlow: StateFlow<TimeSlotEntity?> = combine(
        uiState,
        initFlow.map {
            when (it) {
                is ResultState.Error,
                ResultState.Loading -> null

                is ResultState.Success -> {
                    when (val domainResult = it.data) {
                        is DomainResult.Failure -> TimeSlotEntity.newEntity("")

                        is DomainResult.Success -> domainResult.value
                    }
                }
            }
        }
    ) { uiState: TimeSlotEditUiState, initedResultState: TimeSlotEntity? ->
        val defaultEntity = initedResultState ?: return@combine null
        TimeSlotEntity(
            uuid = defaultEntity.uuid,
            createTime = defaultEntity.createTime,
            title = uiState.slotName,
            startTime = uiState.startTime,
            endTime = uiState.endTime,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        null
    )


    @OptIn(FlowPreview::class)
    val validStateFlow = currentTimeSlotFlow
        .debounce(500)
        .mapNotNull { it }
        .map {
            getTimeSlotValidUseCase.invoke(it, currentRoute.timeRoutineUuid)
        }.scan(
            TimeSlotEditValidUiState()
        ) { acc, domainResult ->
            when (domainResult) {
                is DomainResult.Failure -> {
                    acc.copy(
                        invalidMessage = domainResult.error.toUiText(),
                        enableSaveButton = false
                    )
                }

                is DomainResult.Success -> TimeSlotEditValidUiState(
                    enableSaveButton = true,
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            TimeSlotEditValidUiState()
        )

    private val _uiEvent = MutableSharedFlow<Envelope<TimeSlotEditUiEvent>>()
    val uiEvent: SharedFlow<Envelope<TimeSlotEditUiEvent>> = merge(intentFlow.mapNotNull {
        UiPreEvent.Intent(it.payload)
    }, _uiEvent.map {
        UiPreEvent.Event(it.payload)
    }).mapNotNull { preEvent: UiPreEvent ->
        preEvent.asEvent()
    }.map {
        Envelope(it)
    }.shareIn(
        viewModelScope,
        SharingStarted.Lazily
    )

    fun sendIntent(intent: TimeSlotEditIntent) {
        viewModelScope.launch {
            intentFlow.emit(Envelope(intent))
        }
    }

    private fun handleIntentSideEffect(intent: TimeSlotEditIntent) {
        when (intent) {
            TimeSlotEditIntent.Back -> {
                navigator.back()
            }

            TimeSlotEditIntent.DeleteConfirm -> {
                deleteTimeSlot()
            }

            TimeSlotEditIntent.SaveConfirm -> {
                saveTimeSlot()
            }

            else -> {
                //no work.
            }
        }
    }

    private fun deleteTimeSlot() = flowResultState {
        val timeSlot = currentTimeSlotFlow.mapNotNull { it }.first()
        deleteTimeSlotUseCase.invoke(timeSlot.uuid)
    }.onEach {
        updateLoading(it)
    }.onlyDomainResult().mapNotNull { it }.mapNotNull { domainResult ->
        when (domainResult) {
            is DomainResult.Failure -> TimeSlotEditUiEvent.ShowAlert(
                domainResult.error.toUiText()
            )

            is DomainResult.Success -> TimeSlotEditUiEvent.ShowAlert(
                UiText.MultipleResArgs.create(
                    CommonR.string.message_format_complete, CommonR.string.text_delete
                ),
                TimeSlotEditIntent.Back
            )
        }
    }.onEach {
        _uiEvent.emit(Envelope(it))
    }.launchIn(viewModelScope)

    private fun saveTimeSlot() = flowResultState {
        val timeSlot = currentTimeSlotFlow.mapNotNull { it }.first()
        val routineUuid = currentRoute.timeRoutineUuid
        saveTimeSlotUseCase.invoke(routineUuid, timeSlot)
    }.onEach {
        updateLoading(it)
    }.onlyDomainResult().mapNotNull { it }.mapNotNull { domainResult ->
        when (domainResult) {
            is DomainResult.Failure -> {
                TimeSlotEditUiEvent.ShowAlert(
                    domainResult.error.toUiText(),
                )
            }

            is DomainResult.Success -> {
                TimeSlotEditUiEvent.ShowAlert(
                    UiText.MultipleResArgs.create(
                        CommonR.string.message_format_complete, CommonR.string.text_save
                    ),
                    TimeSlotEditIntent.Back
                )
            }
        }
    }.onEach {
        _uiEvent.emit(Envelope(it))
    }.launchIn(viewModelScope)

    private suspend fun updateLoading(resultState: ResultState<DomainResult<*>>) {
        when (resultState) {
            ResultState.Loading -> _loadingStateFlow.emit(true)
            is ResultState.Error,
            is ResultState.Success<*> -> _loadingStateFlow.emit(false)
        }
    }

    init {
        intentFlow.onEach {
            handleIntentSideEffect(it.payload)
        }.launchIn(viewModelScope)
    }
}

private fun UiPreState.applyTo(
    currentState: TimeSlotEditUiState
): TimeSlotEditUiState = when (this) {
    is UiPreState.Init -> {
        currentState.reduce(this)
    }

    is UiPreState.Intent -> {
        currentState.reduce(this)
    }

    is UiPreState.Loading -> {
        currentState.copy(
            loading = loading
        )
    }
}

private fun UiPreEvent.asEvent(): TimeSlotEditUiEvent? = when (this) {
    is UiPreEvent.Intent -> {
        when (val intent = this.intent) {
            TimeSlotEditIntent.Delete -> TimeSlotEditUiEvent.ShowConfirm(
                UiText.MultipleResArgs.create(
                    CommonR.string.message_format_confirm,
                    CommonR.string.text_delete
                ),
                TimeSlotEditIntent.DeleteConfirm
            )

            TimeSlotEditIntent.Save -> TimeSlotEditUiEvent.ShowConfirm(
                UiText.MultipleResArgs.create(
                    CommonR.string.message_format_confirm,
                    CommonR.string.text_save
                ),
                TimeSlotEditIntent.SaveConfirm
            )

            is TimeSlotEditIntent.SelectTime -> TimeSlotEditUiEvent.SelectTime(
                time = intent.time,
                isStartTime = intent.isStartTime,
            )

            else -> null
        }
    }

    is UiPreEvent.Event -> this.payload
}

private fun TimeSlotEditUiState.reduce(preState: UiPreState.Init): TimeSlotEditUiState =
    when (preState.domainResult) {
        is DomainResult.Failure -> this.copy(
            slotName = "",
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            visibleDelete = false
        )

        is DomainResult.Success -> this.copy(
            slotName = preState.domainResult.value.title,
            startTime = preState.domainResult.value.startTime,
            endTime = preState.domainResult.value.endTime,
            visibleDelete = true
        )
    }

private fun TimeSlotEditUiState.reduce(preState: UiPreState.Intent): TimeSlotEditUiState =
    when (preState.intent) {
        is TimeSlotEditIntent.UpdateSlotName -> {
            copy(slotName = preState.intent.newName)
        }

        is TimeSlotEditIntent.SelectedTime -> {
            if (preState.intent.isStartTime) {
                this.copy(
                    startTime = preState.intent.selectedTime
                )
            } else {
                this.copy(
                    endTime = preState.intent.selectedTime
                )
            }
        }

        else -> this
    }

private sealed interface UiPreState {
    data class Init(
        val domainResult: DomainResult<TimeSlotEntity>,
    ) : UiPreState

    data class Intent(val intent: TimeSlotEditIntent) : UiPreState

    data class Loading(val loading: Boolean) : UiPreState
}

private sealed interface UiPreEvent {
    data class Intent(val intent: TimeSlotEditIntent) : UiPreEvent
    data class Event(val payload: TimeSlotEditUiEvent) : UiPreEvent

}