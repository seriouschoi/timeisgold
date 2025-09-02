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
import software.seriouschoi.timeisgold.core.common.ui.UiText.Res
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.DomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetTimeRoutineUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.bar.R
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditUiEvent.ShowConfirm
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

    private val _uiState = MutableStateFlow<TimeRoutineEditUiState>(TimeRoutineEditUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiIntent = MutableSharedFlow<TimeRoutineEditUiIntent>()
    private val _uiEvent = MutableSharedFlow<TimeRoutineEditUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var internalState = TimeRoutineEditInternalState()

    init {
        viewModelScope.launch {
            getTimeRoutineUseCase(route.dayOfWeek).asResultState().collect {
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
                                message = Res(id = CommonR.string.message_failed_load_data),
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
                        Res(
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
                        Res(
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
                        message = Res(id = CommonR.string.message_not_found_input_data),
                        confirmIntent = TimeRoutineEditUiIntent.Exit,
                    )
                )
                return@launch
            }

            try {
                setTimeRoutineUseCase(
                    routine = TimeRoutineEntity.create(
                        currentRoutineState.routineTitle
                    ),
                    dayOfWeeks = currentRoutineState.dayOfWeekList,
                )
                _uiEvent.emit(
                    TimeRoutineEditUiEvent.ShowAlert(
                        message = Res(id = CommonR.string.message_success_save_data),
                        confirmIntent = TimeRoutineEditUiIntent.Exit,
                    )
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    TimeRoutineEditUiEvent.ShowAlert(
                        message = Res(id = CommonR.string.message_failed_save_data),
                        confirmIntent = TimeRoutineEditUiIntent.Exit,
                    )
                )
            }
        }
    }

    private fun onCollectedTimeRoutine(domainResult: DomainResult<TimeRoutineComposition>) {
        when (domainResult) {
            is DomainResult.Failure -> {
                _uiState.value = TimeRoutineEditUiState.Routine()
            }

            is DomainResult.Success -> {
                val composition = domainResult.value
                internalState = internalState.copy(
                    routineUuid = composition.timeRoutine.uuid
                )

                _uiState.value = TimeRoutineEditUiState.Routine(
                    routineTitle = composition.timeRoutine.title,
                    dayOfWeekList = composition.dayOfWeeks.map {
                        it.dayOfWeek
                    },
                    currentDayOfWeek = route.dayOfWeek
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
}

private data class TimeRoutineEditInternalState(
    val routineUuid: String? = null,
)
