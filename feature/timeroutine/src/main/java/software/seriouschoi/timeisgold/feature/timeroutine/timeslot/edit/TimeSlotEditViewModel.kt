package software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.domain.mapper.onlyDomainResult
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.usecase.timeslot.WatchTimeSlotDetailUseCase
import java.time.LocalTime
import javax.inject.Inject
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

@HiltViewModel
internal class TimeSlotEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val watchTimeSlotDetailUseCase: WatchTimeSlotDetailUseCase,
    private val navigator: DestNavigatorPort,
) : ViewModel() {
    private val currentRoute get() = savedStateHandle.toRoute<TimeSlotEditScreenRoute>()
    private val intentFlow = MutableSharedFlow<Envelope<TimeSlotEditIntent>>()

    private val initFlow = flow {
        val slotUuid = currentRoute.timeSlotUuid
        if (slotUuid != null) {
            val domainResult = watchTimeSlotDetailUseCase.invoke(
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


    val uiState: StateFlow<TimeSlotEditUiState> = merge(
        initFlow.onlyDomainResult()
            .mapNotNull { domainResult: DomainResult<TimeSlotEntity>? ->
                domainResult?.let { UiPreState.Init(it) }
            },
        intentFlow.mapNotNull {
            UiPreState.Intent(it.payload)
        }
    ).scan(TimeSlotEditUiState()) { currentState, preState: UiPreState ->
        when (preState) {
            is UiPreState.Init -> {
                currentState.reduce(preState)
            }

            is UiPreState.Intent -> {
                currentState.reduce(preState)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        TimeSlotEditUiState()
    )

    val uiEvent: SharedFlow<Envelope<TimeSlotEditUiEvent>> = merge(intentFlow.mapNotNull {
        UiPreEvent.Intent(it.payload)
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

            TimeSlotEditIntent.Delete -> TODO()
            TimeSlotEditIntent.Save -> TODO()
            else -> {

            }
        }
    }

    init {
        intentFlow.onEach {
            handleIntentSideEffect(it.payload)
        }.launchIn(viewModelScope)
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
}

private fun TimeSlotEditUiState.reduce(preState: UiPreState.Init): TimeSlotEditUiState =
    when (preState.domainResult) {
        is DomainResult.Failure -> this.copy(
            slotName = "",
            startTime = LocalTime.now(),
            endTime = LocalTime.now()
        )

        is DomainResult.Success -> this.copy(
            slotName = preState.domainResult.value.title,
            startTime = preState.domainResult.value.startTime,
            endTime = preState.domainResult.value.endTime
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
}

private sealed interface UiPreEvent {
    data class Intent(val intent: TimeSlotEditIntent) : UiPreEvent

}