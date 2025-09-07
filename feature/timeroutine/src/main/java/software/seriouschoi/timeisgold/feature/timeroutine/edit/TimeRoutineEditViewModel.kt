package software.seriouschoi.timeisgold.feature.timeroutine.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineCompositionUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetValidTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetTimeRoutineUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.bar.R
import timber.log.Timber
import java.time.DayOfWeek
import java.util.UUID
import javax.inject.Inject
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

@HiltViewModel
internal class TimeRoutineEditViewModel @Inject constructor(
    private val navigator: DestNavigatorPort,

    private val getTimeRoutineCompositionUseCase: GetTimeRoutineCompositionUseCase,
    private val setTimeRoutineUseCase: SetTimeRoutineUseCase,
    private val getValidTimeRoutineUseCase: GetValidTimeRoutineUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route get() = savedStateHandle.toRoute<TimeRoutineEditScreenRoute>()
    private val currentDayOfWeek: DayOfWeek = route.dayOfWeekOrdinal.let {
        DayOfWeek.entries[it]
    }

    private val _uiState = MutableStateFlow<TimeRoutineEditUiState>(TimeRoutineEditUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<TimeRoutineEditUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _uiIntent = MutableSharedFlow<TimeRoutineEditUiIntent>()

    private val timeRoutineState = uiState.mapNotNull {
        uiState.value as? TimeRoutineEditUiState.Routine
    }.map { currentRoutineState: TimeRoutineEditUiState.Routine ->
        val routineFronState = TimeRoutineEntity.create(
            currentRoutineState.routineTitle,
        ).copy(
            uuid = currentRoutineState.routineUuid ?: UUID.randomUUID().toString()
        )
        val currentDayOfWeeks = currentRoutineState.dayOfWeekList.map {
            TimeRoutineDayOfWeekEntity(
                dayOfWeek = it
            )
        }
        TimeRoutineDefinition(
            timeRoutine = routineFronState,
            dayOfWeeks = currentDayOfWeeks.toSet()
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    @OptIn(FlowPreview::class)
    private val validFlow: Flow<ResultState<DomainResult<Boolean>>> =
        timeRoutineState.debounce(500).map { timeRoutine: TimeRoutineDefinition? ->
            timeRoutine?.let {
                getValidTimeRoutineUseCase(it)
            } ?: DomainResult.Success(false)
        }.distinctUntilChanged().asResultState()

    @OptIn(FlowPreview::class)
    fun init() {
        viewModelScope.launch {
            getTimeRoutineCompositionUseCase(currentDayOfWeek).distinctUntilChanged()
                .asResultState()
                .collect { resultState: ResultState<DomainResult<TimeRoutineComposition>> ->
                    _uiState.update {
                        uiState.value.reduceResultState(resultState)
                    }
                    handleGetRoutineSideEffect(resultState)
                }
        }

        viewModelScope.launch {
            _uiIntent.distinctUntilChanged().collect { intent ->
                _uiState.update {
                    it.reduceIntent(intent)
                }
                handleIntentSideEffect(intent)
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

    private fun TimeRoutineEditUiState.reduceValidResultState(
        validResult: ResultState<DomainResult<Boolean>>,
    ): TimeRoutineEditUiState {
        if (this !is TimeRoutineEditUiState.Routine) return this

        return when (validResult) {
            is ResultState.Success -> {
                val domainResult = validResult.data
                val newState = this.validState.reduceValidDomainResult(domainResult)
                this.copy(validState = newState)
            }

            else -> return this
        }
    }

    private fun TimeRoutineEditUiValidUiState.reduceValidDomainResult(validResult: DomainResult<Boolean>): TimeRoutineEditUiValidUiState {
        return when (validResult) {
            is DomainResult.Failure -> {
                val error = validResult.error
                val newState = this.copy(
                    isValid = false
                )
                when (error) {
                    DomainError.Validation.Title -> {
                        newState.copy(
                            invalidTitleMessage = error.toUiText()
                        )
                    }

                    DomainError.Validation.NoSelectedDayOfWeek,
                    DomainError.Conflict.DayOfWeek,
                        -> {
                        newState.copy(
                            invalidDayOfWeekMessage = error.toUiText()
                        )
                    }

                    else -> {
                        newState
                    }
                }
            }

            is DomainResult.Success -> {
                if (validResult.value) {
                    TimeRoutineEditUiValidUiState(isValid = true)
                }
                else {
                    this.copy(isValid = false)
                }
            }
        }
    }

    private suspend fun handleGetRoutineSideEffect(
        state: ResultState<DomainResult<TimeRoutineComposition>>,
    ) {
        when (state) {
            is ResultState.Error -> {
                _uiEvent.emit(
                    TimeRoutineEditUiEvent.ShowAlert(
                        message = UiText.Res(id = CommonR.string.message_failed_load_data),
                        confirmIntent = TimeRoutineEditUiIntent.Exit(),
                    )
                )
            }

            else -> {
            }
        }
    }

    private fun TimeRoutineEditUiState.reduceResultState(
        resultState: ResultState<DomainResult<TimeRoutineComposition>>,
    ): TimeRoutineEditUiState {
        return when (resultState) {
            is ResultState.Loading -> {
                TimeRoutineEditUiState.Loading
            }

            is ResultState.Success -> {
                createRoutineState(resultState.data)
            }

            else -> this
        }
    }

    private fun createRoutineState(
        data: DomainResult<TimeRoutineComposition>,
    ): TimeRoutineEditUiState {
        Timber.d("createRoutineState data=$data")
        val routineState = TimeRoutineEditUiState.Routine(
            currentDayOfWeek = currentDayOfWeek,
            dayOfWeekList = setOf(currentDayOfWeek),
        )
        return when (data) {
            is DomainResult.Failure -> routineState
            is DomainResult.Success -> {
                val domainResult: TimeRoutineComposition = data.value
                routineState.reduceRoutineComposition(domainResult)
            }
        }
    }


    private fun saveTimeRoutine() {
        viewModelScope.launch {
            val timeRoutine = timeRoutineState.value ?: return@launch
            val result = setTimeRoutineUseCase(
                timeRoutine
            )
            val event = result.toSaveResultToEvent()
            _uiEvent.emit(event)
        }
    }

    private fun DomainError.toUiText(): UiText = when (this) {
        is DomainError.Validation -> {
            when (this) {
                DomainError.Validation.NoSelectedDayOfWeek -> UiText.Res(
                    id = R.string.message_dayofweek_is_empty
                )

                DomainError.Validation.Title -> UiText.Res(
                    id = R.string.message_title_is_empty
                )
            }
        }

        is DomainError.Conflict -> {
            when (this) {
                DomainError.Conflict.DayOfWeek -> UiText.Res(
                    id = R.string.message_conflict_dayofweek
                )

                else -> {
                    UiText.Res(
                        id = CommonR.string.message_failed_save_data
                    )
                }
            }
        }

        is DomainError.NotFound,
        is DomainError.Technical,
            -> UiText.Res(
            id = CommonR.string.message_failed_save_data
        )
    }

    private fun DomainResult<*>.toSaveResultToEvent(): TimeRoutineEditUiEvent =
        when (this) {
            is DomainResult.Success -> TimeRoutineEditUiEvent.ShowAlert(
                message = UiText.Res(id = CommonR.string.message_success_save_data),
                confirmIntent = TimeRoutineEditUiIntent.Exit()
            )

            is DomainResult.Failure -> {
                Timber.d("saveResult failed. $this")
                TimeRoutineEditUiEvent.ShowAlert(
                    message = this.error.toUiText(),
                    confirmIntent = null
                )
            }
        }

    private fun TimeRoutineEditUiState.Routine.reduceRoutineComposition(
        routineComposition: TimeRoutineComposition,
    ): TimeRoutineEditUiState {
        val newDayOfWeekList = routineComposition.dayOfWeeks.map {
            it.dayOfWeek
        }
        return this.copy(
            dayOfWeekList = listOf(
                newDayOfWeekList,
                this.dayOfWeekList
            ).flatten().toSet(),
            routineTitle = routineComposition.timeRoutine.title,
            routineUuid = routineComposition.timeRoutine.uuid
        )
    }

    fun sendIntent(intent: TimeRoutineEditUiIntent) {
        viewModelScope.launch {
            Timber.d("sendIntent $intent")
            _uiIntent.emit(intent)
        }
    }

    private suspend fun handleIntentSideEffect(intent: TimeRoutineEditUiIntent) {
        when (intent) {
            is TimeRoutineEditUiIntent.Save -> {
                _uiEvent.emit(
                    TimeRoutineEditUiEvent.ShowConfirm(
                        UiText.Res(
                            R.string.message_routine_edit_confirm
                        ),
                        TimeRoutineEditUiIntent.SaveConfirm(),
                        null
                    )
                )
            }

            is TimeRoutineEditUiIntent.Cancel -> {
                _uiEvent.emit(
                    TimeRoutineEditUiEvent.ShowConfirm(
                        UiText.Res(
                            R.string.message_routine_edit_cancel
                        ),
                        TimeRoutineEditUiIntent.Exit(),
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

            else -> {}
        }
    }

    private fun TimeRoutineEditUiState.reduceIntent(
        intent: TimeRoutineEditUiIntent,
    ): TimeRoutineEditUiState {
        return when (intent) {
            is TimeRoutineEditUiIntent.UpdateDayOfWeek -> this.reduceIntentDayOfWeek(intent)
            is TimeRoutineEditUiIntent.UpdateRoutineTitle -> this.reduceIntentTitle(intent)
            else -> this
        }
    }

    private fun TimeRoutineEditUiState.reduceIntentTitle(
        intent: TimeRoutineEditUiIntent.UpdateRoutineTitle,
    ): TimeRoutineEditUiState {
        val currentRoutineState = (this as? TimeRoutineEditUiState.Routine)
        return currentRoutineState?.copy(
            routineTitle = intent.title
        ) ?: this
    }

    private fun TimeRoutineEditUiState.reduceIntentDayOfWeek(
        intent: TimeRoutineEditUiIntent.UpdateDayOfWeek,
    ): TimeRoutineEditUiState {
        val routineState = this as? TimeRoutineEditUiState.Routine ?: return this

        val newDayOfWeeks = routineState.dayOfWeekList.toMutableSet()
        if (intent.checked) {
            newDayOfWeeks.add(intent.dayOfWeek)
        } else {
            newDayOfWeeks.remove(intent.dayOfWeek)
        }
        return routineState.copy(
            dayOfWeekList = newDayOfWeeks
        )
    }

    data class SaveRequestData(
        val title: String,
        val dayOfWeeks: Set<DayOfWeek>,
    )
}