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
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.domain.data.ConflictCode
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.ValidationCode
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetTimeRoutineUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.bar.R
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditUiEvent.ShowConfirm
import java.time.DayOfWeek
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

    private val _uiIntent = MutableSharedFlow<TimeRoutineEditUiIntent>()
    private val _uiEvent = MutableSharedFlow<TimeRoutineEditUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var internalState = TimeRoutineEditInternalState()

    init {
        viewModelScope.launch {
            getTimeRoutineUseCase(currentDayOfWeek).asResultState().collect {
                when (it) {
                    is ResultState.Loading -> {
                        _uiState.value = TimeRoutineEditUiState.Loading
                    }

                    is ResultState.Success -> {
                        onCollectedTimeRoutine(it.data)

                    }

                    is ResultState.Error -> {
                        _uiEvent.emit(
                            TimeRoutineEditUiEvent.ShowAlert(
                                message = UiText.Res(id = CommonR.string.message_failed_load_data),
                                confirmIntent = TimeRoutineEditUiIntent.Exit,
                            )
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            _uiIntent.collect {
                onCollectedIntent(it)
            }
        }
    }

    private suspend fun onCollectedIntent(intent: TimeRoutineEditUiIntent) {
        when (intent) {
            is TimeRoutineEditUiIntent.Save -> {
                _uiEvent.emit(
                    ShowConfirm(
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
                    ShowConfirm(
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

            val result = setTimeRoutineUseCase(
                routine = TimeRoutineEntity.create(
                    currentRoutineState.routineTitle
                ),
                dayOfWeeks = currentRoutineState.dayOfWeekList.toList(),
            )
            val event = mapSaveResultToEvent(result)
            _uiEvent.emit(event)
        }
    }

    private fun DomainError.toUiText(): UiText = when (this) {
        is DomainError.Validation -> {
            when (this.code) {
                ValidationCode.TimeRoutine.DayOfWeekEmpty -> UiText.Res(
                    id = R.string.message_dayofweek_is_empty
                )

                ValidationCode.TimeRoutine.Title -> UiText.Res(
                    id = R.string.message_title_is_empty
                )
            }
        }

        is DomainError.Conflict -> {
            when (this.code) {
                ConflictCode.TimeRoutine.DayOfWeek -> UiText.Res(
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

    private fun onCollectedTimeRoutine(domainResult: DomainResult<TimeRoutineComposition>) {
        val routineState = TimeRoutineEditUiState.Routine(
            currentDayOfWeek = currentDayOfWeek,
            dayOfWeekList = setOf(currentDayOfWeek)
        )
        _uiState.value = when (domainResult) {
            is DomainResult.Failure -> {
                routineState
            }

            is DomainResult.Success -> {
                val composition = domainResult.value
                internalState = internalState.copy(
                    routineUuid = composition.timeRoutine.uuid
                )

                val newDayOfWeekList = composition.dayOfWeeks.map {
                    it.dayOfWeek
                }
                routineState.copy(
                    dayOfWeekList = listOf(
                        newDayOfWeekList,
                        routineState.dayOfWeekList
                    ).flatten().toSet(),
                    routineTitle = composition.timeRoutine.title
                )
            }
        }
    }

    fun updateRoutineTitle(newTitle: String) {
        val currentRoutineState = (uiState.value as? TimeRoutineEditUiState.Routine)
        if (currentRoutineState == null) {
            return
        }
        _uiState.value = currentRoutineState.copy(
            routineTitle = newTitle
        )
    }

    fun sendIntent(intent: TimeRoutineEditUiIntent) {
        viewModelScope.launch {
            _uiIntent.emit(intent)
        }
    }

    fun checkDayOfWeek(dayOfWeek: DayOfWeek, check: Boolean) {
        viewModelScope.launch {
            val routineState = uiState.value as? TimeRoutineEditUiState.Routine ?: return@launch

            val newDayOfWeeks = routineState.dayOfWeekList.toMutableSet()
            if(check) {
                newDayOfWeeks.add(dayOfWeek)
            } else {
                newDayOfWeeks.remove(dayOfWeek)
            }
            _uiState.value = routineState.copy(
                dayOfWeekList = newDayOfWeeks
            )
        }
    }

}

private data class TimeRoutineEditInternalState(
    val routineUuid: String? = null,
)
