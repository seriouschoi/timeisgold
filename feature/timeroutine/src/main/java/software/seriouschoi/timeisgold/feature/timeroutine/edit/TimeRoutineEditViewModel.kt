package software.seriouschoi.timeisgold.feature.timeroutine.edit

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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
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

@OptIn(FlowPreview::class)
@HiltViewModel
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

    private val initResultStateFlow: StateFlow<ResultState<DomainResult<TimeRoutineDefinition>>> =
        flow {
            val result = getTimeRoutineUseCase.invoke(currentDayOfWeek).first()
            emit(result)
        }.asResultState().stateIn(viewModelScope, SharingStarted.Lazily, ResultState.Loading)


    val uiStateFlow: StateFlow<TimeRoutineEditUiState> = merge(
        initResultStateFlow.map { it: ResultState<DomainResult<TimeRoutineDefinition>> ->
            UiPreState.Init(it)
        }, _uiIntentFlow.map {
            UiPreState.Intent(it.payload)
        }
    ).scan(
        TimeRoutineEditUiState(
            isLoading = true
        )
    ) { currentState: TimeRoutineEditUiState, preState: UiPreState ->
        when (preState) {
            is UiPreState.Init -> {
                currentState.reduceFromInit(
                    preState = preState,
                    currentDayOfWeek = currentDayOfWeek,
                )
            }

            is UiPreState.Intent -> {
                currentState.reduceFromIntent(preState)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, TimeRoutineEditUiState(isLoading = true))


    /**
     * 현재 입력된 routineDefinition
     */
    private val currentRoutineDefinitionFlow: StateFlow<TimeRoutineDefinition?> = combine(
        initResultStateFlow.mapNotNull { resultState ->
            when (resultState) {
                is ResultState.Error -> null
                ResultState.Loading -> null
                is ResultState.Success -> {
                    resultState.data
                }
            }
        }.map { domainResult: DomainResult<TimeRoutineDefinition> ->
            when (domainResult) {
                is DomainResult.Failure -> {
                    when (domainResult.error) {
                        DomainError.NotFound.TimeRoutine -> emptyTimeRoutineDefinition
                        else -> null
                    }
                }

                is DomainResult.Success -> domainResult.value
            }
        }, uiStateFlow
    ) { initResult: TimeRoutineDefinition?, ui ->
        val def = initResult ?: return@combine null

        val routine = def.timeRoutine.copy(
            title = ui.routineTitle
        )
        val days = ui.dayOfWeekList.map {
            TimeRoutineDayOfWeekEntity(
                dayOfWeek = it
            )
        }.toSet()
        def.copy(
            timeRoutine = routine,
            dayOfWeeks = days
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val validStateFlow: StateFlow<TimeRoutineEditUiValidUiState> = currentRoutineDefinitionFlow
        .debounce(500)
        .mapNotNull { timeRoutine: TimeRoutineDefinition? ->
            timeRoutine?.let { getValidTimeRoutineUseCase.invoke(it) }
        }.asResultState()
        .mapNotNull { resultState: ResultState<DomainResult<Boolean>> ->
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

    private val _uiEvent = MutableSharedFlow<TimeRoutineEditUiEvent>()
    val uiEvent: SharedFlow<Envelope<TimeRoutineEditUiEvent>> = merge(
        initResultStateFlow.map {
            UiPreEvent.Init(it)
        }, _uiIntentFlow.map {
            UiPreEvent.Intent(it.payload)
        }, _uiEvent.map {
            UiPreEvent.Event(it)
        }
    ).mapNotNull { preEvent: UiPreEvent ->
        when (preEvent) {
            is UiPreEvent.Init -> {
                createEvent(preEvent)
            }

            is UiPreEvent.Intent -> {
                createEvent(preEvent)
            }

            is UiPreEvent.Event -> {
                preEvent.event
            }
        }
    }.map {
        Envelope(it)
    }.shareIn(viewModelScope, SharingStarted.Lazily)

    fun init() {
        viewModelScope.launch {
            _uiIntentFlow.collect { envelope: Envelope<TimeRoutineEditUiIntent> ->
                when (envelope.payload) {
                    TimeRoutineEditUiIntent.Exit -> navigator.back()
                    TimeRoutineEditUiIntent.SaveConfirm -> saveTimeRoutine()
                    TimeRoutineEditUiIntent.DeleteConfirm -> deleteTimeRoutine()
                    else -> {
                        //no work.
                    }
                }
            }
        }
    }


    private fun saveTimeRoutine() {
        viewModelScope.launch {
            val timeRoutine = currentRoutineDefinitionFlow.firstOrNull() ?: return@launch
            val result = setTimeRoutineUseCase.invoke(
                timeRoutine
            )
            val event = result.toSaveResultToEvent()
            _uiEvent.emit(event)
        }
    }


    fun sendIntent(intent: TimeRoutineEditUiIntent) {
        viewModelScope.launch {
            _uiIntentFlow.emit(Envelope(intent))
        }
    }


    private fun deleteTimeRoutine() {
        viewModelScope.launch {
            val routineDefinition = currentRoutineDefinitionFlow.first() ?: return@launch
            val result =
                deleteTimeRoutineUseCase.invoke(routineDefinition.timeRoutine.uuid)
            val event = result.toDeleteResultToEvent()
            _uiEvent.emit(event)
        }
    }

}

private fun TimeRoutineEditUiState.reduceFromIntent(
    preState: UiPreState.Intent,
): TimeRoutineEditUiState {
    return when (preState.intent) {
        is TimeRoutineEditUiIntent.UpdateDayOfWeek -> {
            val newDayOfWeeks = this.dayOfWeekList.toMutableSet().apply {
                if (preState.intent.checked) {
                    this.add(preState.intent.dayOfWeek)
                } else {
                    this.remove(preState.intent.dayOfWeek)
                }
            }
            this.copy(
                dayOfWeekList = newDayOfWeeks
            )
        }

        is TimeRoutineEditUiIntent.UpdateRoutineTitle -> {
            this.copy(
                routineTitle = preState.intent.title
            )
        }

        else -> this
    }
}

private fun TimeRoutineEditUiState.reduceFromInit(
    preState: UiPreState.Init,
    currentDayOfWeek: DayOfWeek,
): TimeRoutineEditUiState {
    val data = preState.data
    val emptyState = TimeRoutineEditUiState(
        isLoading = false,
        routineTitle = "",
        dayOfWeekList = emptySet(),
        currentDayOfWeek = currentDayOfWeek,
        visibleDelete = false
    )
    return when (data) {
        is ResultState.Error -> {
            emptyState
        }

        ResultState.Loading -> this.copy(
            isLoading = true
        )

        is ResultState.Success -> {
            when (val domainResult = data.data) {
                is DomainResult.Failure -> {
                    emptyState
                }

                is DomainResult.Success -> {
                    val routineDef = domainResult.value
                    TimeRoutineEditUiState(
                        routineTitle = routineDef.timeRoutine.title,
                        dayOfWeekList = routineDef.dayOfWeeks.map { it.dayOfWeek }.toSet(),
                        currentDayOfWeek = currentDayOfWeek,
                        visibleDelete = true,
                        isLoading = false

                    )
                }
            }
        }
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

private fun createEvent(preEvent: UiPreEvent.Intent): TimeRoutineEditUiEvent? {
    val intent = preEvent.intent
    return when (intent) {
        TimeRoutineEditUiIntent.Save -> {
            TimeRoutineEditUiEvent.ShowConfirm(
                UiText.MultipleRes.create(
                    CommonR.string.message_format_confirm,
                    CommonR.string.text_save
                ),
                TimeRoutineEditUiIntent.SaveConfirm,
                null
            )
        }

        TimeRoutineEditUiIntent.Delete -> {
            TimeRoutineEditUiEvent.ShowConfirm(
                UiText.MultipleRes.create(
                    CommonR.string.message_format_confirm,
                    CommonR.string.text_delete
                ),
                TimeRoutineEditUiIntent.DeleteConfirm,
                null
            )
        }

        TimeRoutineEditUiIntent.Cancel -> {
            TimeRoutineEditUiEvent.ShowConfirm(
                UiText.MultipleRes.create(
                    CommonR.string.message_format_confirm,
                    CommonR.string.text_cancel
                ),
                TimeRoutineEditUiIntent.Exit,
                null
            )
        }

        else -> null
    }
}

private fun createEvent(preEvent: UiPreEvent.Init): TimeRoutineEditUiEvent? {
    val newEvent: TimeRoutineEditUiEvent? = when (
        val resultState: ResultState<DomainResult<TimeRoutineDefinition>> = preEvent.data
    ) {
        is ResultState.Error -> {
            null
        }

        ResultState.Loading -> {
            null
        }

        is ResultState.Success -> {
            when (val domainResult = resultState.data) {
                is DomainResult.Success -> null
                is DomainResult.Failure -> {
                    when (val domainError = domainResult.error) {
                        DomainError.Conflict.Data,
                        DomainError.Conflict.DayOfWeek,
                        DomainError.Technical.Unknown,
                        DomainError.Validation.EmptyTitle,
                        DomainError.Validation.NoSelectedDayOfWeek,
                            -> {
                            TimeRoutineEditUiEvent.ShowAlert(
                                message = domainError.toUiText(),
                                confirmIntent = TimeRoutineEditUiIntent.Exit,
                            )
                        }

                        DomainError.NotFound.TimeRoutine -> null
                    }
                }
            }
        }
    }
    return newEvent
}

private sealed interface UiPreState {
    data class Init(val data: ResultState<DomainResult<TimeRoutineDefinition>>) : UiPreState
    data class Intent(val intent: TimeRoutineEditUiIntent) : UiPreState
}

private sealed interface UiPreEvent {
    data class Init(val data: ResultState<DomainResult<TimeRoutineDefinition>>) : UiPreEvent
    data class Intent(val intent: TimeRoutineEditUiIntent) : UiPreEvent
    data class Event(val event: TimeRoutineEditUiEvent) : UiPreEvent
}