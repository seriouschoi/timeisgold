package software.seriouschoi.timeisgold.feature.timeroutine.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.domain.mapper.toUiText
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.DeleteTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineDefinitionUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetValidTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetTimeRoutineUseCase
import timber.log.Timber
import java.time.DayOfWeek
import javax.inject.Inject
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

@HiltViewModel
@OptIn(FlowPreview::class)
internal class TimeRoutineEditViewModel @Inject constructor(
    private val navigator: DestNavigatorPort,

    private val getTimeRoutineUseCase: GetTimeRoutineDefinitionUseCase,
    private val setTimeRoutineUseCase: SetTimeRoutineUseCase,
    private val deleteTimeRoutineUseCase: DeleteTimeRoutineUseCase,
    private val getValidTimeRoutineUseCase: GetValidTimeRoutineUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route get() = savedStateHandle.toRoute<TimeRoutineEditScreenRoute>()
    private val currentDayOfWeek: DayOfWeek = route.dayOfWeekOrdinal.let {
        DayOfWeek.entries[it]
    }

    private val emptyTimeRoutineDefinition: TimeRoutineDefinition = TimeRoutineDefinition(
        timeRoutine = TimeRoutineEntity.create(title = ""),
        dayOfWeeks = listOf(currentDayOfWeek).map {
            TimeRoutineDayOfWeekEntity(it)
        }.toSet()
    )

    private val _uiIntentFlow = MutableSharedFlow<Envelope<TimeRoutineEditUiIntent>>()

    private val initResultStateFlow = flow {
        val result = getTimeRoutineUseCase.invoke(currentDayOfWeek).first()
        val initData = when (result) {
            is DomainResult.Failure -> {
                null
            }

            is DomainResult.Success -> result.value
        }
        emit(initData)
    }.asResultState().stateIn(viewModelScope, SharingStarted.Lazily, ResultState.Loading)


    private val initedRoutineFlow = initResultStateFlow.map { resultState ->
        when (resultState) {
            is ResultState.Error -> null
            ResultState.Loading -> null
            is ResultState.Success -> {
                resultState.data
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val uiState: StateFlow<TimeRoutineEditUiState> = merge(
        initedRoutineFlow.map {
            UiPreState.Init(it ?: emptyTimeRoutineDefinition)
        }, _uiIntentFlow.map {
            UiPreState.Intent(it.payload)
        }
    ).scan(
        TimeRoutineEditUiState.Routine()
    ) { currentState: TimeRoutineEditUiState.Routine, ui: UiPreState ->
        when (ui) {
            is UiPreState.Init -> {
                TODO()
            }
            is UiPreState.Intent -> {
                TODO()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, TimeRoutineEditUiState.Routine())


    private val currentRoutineState: StateFlow<TimeRoutineDefinition> = combine(
        initedRoutineFlow, uiState
    ) { def, ui ->
        val routineDefinition = def ?: emptyTimeRoutineDefinition
        if (ui is TimeRoutineEditUiState.Routine) {
            val routine = routineDefinition.timeRoutine.copy(
                title = ui.routineTitle
            )
            val days = ui.dayOfWeekList.map {
                TimeRoutineDayOfWeekEntity(
                    dayOfWeek = it
                )
            }.toSet()
            routineDefinition.copy(
                timeRoutine = routine,
                dayOfWeeks = days
            )
        } else {
            routineDefinition
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyTimeRoutineDefinition)

    val validStateFlow: StateFlow<TimeRoutineEditUiValidUiState> =
        currentRoutineState.debounce(500).map { timeRoutine: TimeRoutineDefinition ->
            getValidTimeRoutineUseCase.invoke(timeRoutine)
        }.asResultState().mapNotNull { resultState: ResultState<DomainResult<Boolean>> ->
            when (resultState) {
                is ResultState.Success -> resultState.data
                is ResultState.Error -> DomainResult.Failure(DomainError.Technical.Unknown)
                ResultState.Loading -> null
            }
        }.map { domainResult: DomainResult<Boolean> ->
            when (domainResult) {
                is DomainResult.Failure -> {
                    val invalidState = TimeRoutineEditUiValidUiState(
                        isValid = false,
                    )
                    when (val error = domainResult.error) {
                        DomainError.Validation.EmptyTitle,
                        DomainError.NotFound.TimeRoutine,
                        DomainError.Conflict.Data,
                        DomainError.Technical.Unknown,
                            -> {
                            invalidState.copy(
                                invalidTitleMessage = error.toUiText(),
                            )
                        }

                        DomainError.Validation.NoSelectedDayOfWeek,
                        DomainError.Conflict.DayOfWeek,
                            -> {
                            invalidState.copy(
                                invalidDayOfWeekMessage = error.toUiText(),
                            )
                        }
                    }
                }

                is DomainResult.Success -> {
                    TimeRoutineEditUiValidUiState(isValid = domainResult.value)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, TimeRoutineEditUiValidUiState())

    private val _uiEvent = MutableSharedFlow<Envelope<TimeRoutineEditUiEvent>>()


    val uiEvent = _uiEvent.asSharedFlow()

    @Deprecated("use validStateFlow")
    private val validFlow: Flow<ResultState<DomainResult<Boolean>>> =
        currentRoutineState.debounce(500).map { timeRoutine: TimeRoutineDefinition ->
            timeRoutine.let {
                getValidTimeRoutineUseCase.invoke(it)
            }
        }.asResultState().distinctUntilChanged()

    fun init() {
        viewModelScope.launch {
            _uiState.emit(TimeRoutineEditUiState.Routine(isLoading = true))
            val routineDomainResult = getTimeRoutineUseCase.invoke(currentDayOfWeek).first()
            initedRoutineFlow.update {
                it.reduceDomainResult(routineDomainResult)
            }
            _uiState.update {
                it.reduceRoutineDomainResult(routineDomainResult, currentDayOfWeek)
            }
            handleGetRoutineSideEffect(routineDomainResult)?.let { event: TimeRoutineEditUiEvent ->
                sendEvent(event)
            }
        }

        viewModelScope.launch {
            _uiIntentFlow.collect { envelope ->
                _uiState.update {
                    it.reduceIntent(envelope.payload)
                }
                handleIntentSideEffect(envelope.payload)
            }
        }

        viewModelScope.launch {
            validFlow.distinctUntilChanged().collect { valid ->
                _uiState.update {
                    it.reduceValidResultState(valid)
                }
            }
        }
    }

    private fun handleGetRoutineSideEffect(state: DomainResult<TimeRoutineDefinition>) =
        when (state) {
            is DomainResult.Failure -> {
                handleGetRoutineError(state.error)
            }

            is DomainResult.Success -> {
                // no work.
                null
            }
        }

    private fun handleGetRoutineError(error: DomainError) = when (error) {
        DomainError.NotFound.TimeRoutine -> {
            null
        }

        DomainError.Conflict.Data,
        DomainError.Conflict.DayOfWeek,
        DomainError.Technical.Unknown,
        DomainError.Validation.NoSelectedDayOfWeek,
        DomainError.Validation.EmptyTitle,
            -> {
            TimeRoutineEditUiEvent.ShowAlert(
                message = error.toUiText(),
                confirmIntent = TimeRoutineEditUiIntent.Exit,
            )
        }
    }

    private fun saveTimeRoutine() {
        viewModelScope.launch {
            val timeRoutine = currentRoutineState.firstOrNull() ?: return@launch
            val result = setTimeRoutineUseCase.invoke(
                timeRoutine
            )
            val event = result.toSaveResultToEvent()
            sendEvent(event)
        }
    }


    fun sendIntent(intent: TimeRoutineEditUiIntent) {
        viewModelScope.launch {
            _uiIntentFlow.emit(Envelope(intent))
        }
    }

    private fun sendEvent(event: TimeRoutineEditUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(Envelope(event))
        }
    }

    private fun handleIntentSideEffect(intent: TimeRoutineEditUiIntent) {
        when (intent) {
            is TimeRoutineEditUiIntent.Save -> {
                sendEvent(
                    TimeRoutineEditUiEvent.ShowConfirm(
                        UiText.MultipleRes.create(
                            CommonR.string.message_format_confirm,
                            CommonR.string.text_save
                        ),
                        TimeRoutineEditUiIntent.SaveConfirm,
                        null
                    )
                )
            }

            is TimeRoutineEditUiIntent.Delete -> {
                sendEvent(
                    TimeRoutineEditUiEvent.ShowConfirm(
                        UiText.MultipleRes.create(
                            CommonR.string.message_format_confirm,
                            CommonR.string.text_delete
                        ),
                        TimeRoutineEditUiIntent.DeleteConfirm,
                        null
                    )
                )
            }

            is TimeRoutineEditUiIntent.Cancel -> {
                sendEvent(
                    TimeRoutineEditUiEvent.ShowConfirm(
                        UiText.MultipleRes.create(
                            CommonR.string.message_format_confirm,
                            CommonR.string.text_cancel
                        ),
                        TimeRoutineEditUiIntent.Exit,
                        null
                    )
                )
            }

            is TimeRoutineEditUiIntent.Exit -> {
                navigator.back()
            }

            is TimeRoutineEditUiIntent.SaveConfirm -> {
                saveTimeRoutine()
            }

            is TimeRoutineEditUiIntent.DeleteConfirm -> {
                deleteTimeRoutine()
            }

            else -> {}
        }
    }

    private fun deleteTimeRoutine() {
        viewModelScope.launch {
            val result = deleteTimeRoutineUseCase.invoke(currentRoutineState.value.timeRoutine.uuid)
            val event = result.toDeleteResultToEvent()
            sendEvent(event)
        }
    }

}


private fun TimeRoutineDefinition.reduceDomainResult(domainResult: DomainResult<TimeRoutineDefinition>) =
    when (domainResult) {
        is DomainResult.Failure -> {
            this
        }

        is DomainResult.Success -> {
            Timber.d("reduceDomainResult success. ${domainResult.value}")
            domainResult.value
        }
    }

private fun DomainResult<*>.toSaveResultToEvent(): TimeRoutineEditUiEvent =
    when (this) {
        is DomainResult.Success -> TimeRoutineEditUiEvent.ShowAlert(
            message = UiText.MultipleRes.create(
                CommonR.string.message_format_complete,
                CommonR.string.text_save
            ),
            confirmIntent = TimeRoutineEditUiIntent.Exit
        )

        is DomainResult.Failure -> {
            Timber.d("saveResult failed. $this")
            TimeRoutineEditUiEvent.ShowAlert(
                message = this.error.toUiText(),
                confirmIntent = null
            )
        }
    }

private fun DomainResult<Boolean>.toDeleteResultToEvent(): TimeRoutineEditUiEvent {
    return when (this) {
        is DomainResult.Success -> TimeRoutineEditUiEvent.ShowAlert(
            message = UiText.MultipleRes.create(
                CommonR.string.message_format_complete,
                CommonR.string.text_delete
            ),
            confirmIntent = TimeRoutineEditUiIntent.Exit
        )

        is DomainResult.Failure -> {
            TimeRoutineEditUiEvent.ShowAlert(
                message = this.error.toUiText(),
                confirmIntent = null
            )
        }
    }
}

private sealed interface UiPreState {
    data class Init(val data: TimeRoutineDefinition) : UiPreState
    data class Intent(val intent: TimeRoutineEditUiIntent) : UiPreState
}