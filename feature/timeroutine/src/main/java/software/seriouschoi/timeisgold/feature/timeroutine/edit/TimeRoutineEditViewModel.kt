package software.seriouschoi.timeisgold.feature.timeroutine.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetTimeRoutineUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.bar.R
import java.time.DayOfWeek
import java.util.UUID
import javax.inject.Inject
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

@HiltViewModel
internal class TimeRoutineEditViewModel @Inject constructor(
    private val navigator: DestNavigatorPort,
    private val getTimeRoutineUseCase: GetTimeRoutineUseCase,
    private val setTimeRoutineUseCase: SetTimeRoutineUseCase,
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

    init {
        viewModelScope.launch {
            getTimeRoutineUseCase(currentDayOfWeek).asResultState()
                .collect { resultState: ResultState<DomainResult<TimeRoutineComposition>> ->
                    _uiState.update {
                        uiState.value.reduceResultState(resultState)
                    }
                    handleGetRoutineSideEffect(resultState)
                }
        }
    }

    private suspend fun handleGetRoutineSideEffect(
        state: ResultState<DomainResult<TimeRoutineComposition>>
    ) {
        when (state) {
            is ResultState.Error -> {
                _uiEvent.emit(
                    TimeRoutineEditUiEvent.ShowAlert(
                        message = UiText.Res(id = CommonR.string.message_failed_load_data),
                        confirmIntent = TimeRoutineEditUiIntent.Exit,
                    )
                )
            }

            else -> {
            }
        }
    }

    private fun TimeRoutineEditUiState.reduceResultState(
        resultState: ResultState<DomainResult<TimeRoutineComposition>>
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
        data: DomainResult<TimeRoutineComposition>
    ): TimeRoutineEditUiState {
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
            val currentRoutineState = (uiState.value as? TimeRoutineEditUiState.Routine)
            if (currentRoutineState == null) {
                _uiEvent.emit(
                    TimeRoutineEditUiEvent.ShowAlert(
                        message = UiText.Res(id = CommonR.string.message_not_found_input_data),
                        confirmIntent = TimeRoutineEditUiIntent.Exit,
                    )
                )
                return@launch
            }

            val routineFronState = TimeRoutineEntity.create(
                currentRoutineState.routineTitle,
            ).copy(
                uuid = currentRoutineState.routineUuid ?: UUID.randomUUID().toString()
            )

            val result = setTimeRoutineUseCase(
                routine = routineFronState,
                dayOfWeeks = currentRoutineState.dayOfWeekList.toList(),
            )
            val event = mapSaveResultToEvent(result)
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

    private fun mapSaveResultToEvent(result: DomainResult<*>): TimeRoutineEditUiEvent =
        when (result) {
            is DomainResult.Success -> TimeRoutineEditUiEvent.ShowAlert(
                message = UiText.Res(id = CommonR.string.message_success_save_data),
                confirmIntent = TimeRoutineEditUiIntent.Exit
            )

            is DomainResult.Failure -> TimeRoutineEditUiEvent.ShowAlert(
                message = result.error.toUiText(),
                confirmIntent = null
            )
        }

    private fun TimeRoutineEditUiState.Routine.reduceRoutineComposition(
        routineComposition: TimeRoutineComposition
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
            _uiState.update {
                it.reduceIntent(intent)
            }
            handleIntentSideEffect(intent)
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
                        TimeRoutineEditUiIntent.SaveConfirm,
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
                        TimeRoutineEditUiIntent.Exit,
                        null
                    )
                )
            }

            TimeRoutineEditUiIntent.Exit -> {
                navigator.back()
            }

            TimeRoutineEditUiIntent.SaveConfirm -> {
                saveTimeRoutine()
            }

            else -> {}
        }
    }

    private fun TimeRoutineEditUiState.reduceIntent(
        intent: TimeRoutineEditUiIntent
    ): TimeRoutineEditUiState {
        return when (intent) {
            is TimeRoutineEditUiIntent.UpdateDayOfWeek -> this.reduceIntentDayOfWeek(intent)
            is TimeRoutineEditUiIntent.UpdateRoutineTitle -> this.reduceIntentTitle(intent)
            else -> this
        }
    }

    private fun TimeRoutineEditUiState.reduceIntentTitle(
        intent: TimeRoutineEditUiIntent.UpdateRoutineTitle
    ): TimeRoutineEditUiState {
        val currentRoutineState = (this as? TimeRoutineEditUiState.Routine)
        return currentRoutineState?.copy(
            routineTitle = intent.title
        ) ?: this
    }

    private fun TimeRoutineEditUiState.reduceIntentDayOfWeek(
        intent: TimeRoutineEditUiIntent.UpdateDayOfWeek
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
}