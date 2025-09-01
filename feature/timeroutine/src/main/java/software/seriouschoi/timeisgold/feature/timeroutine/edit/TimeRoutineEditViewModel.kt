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
import software.seriouschoi.timeisgold.core.common.ui.UiText.Res
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.SetTimeRoutineUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.bar.R
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditUiEvent.ShowConfirm
import java.lang.Exception
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

    private val _uiState = MutableStateFlow<TimeRoutineEditUiState>(TimeRoutineEditUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiIntent = MutableSharedFlow<TimeRoutineEditUiIntent>()
    private val _uiEvent = MutableSharedFlow<TimeRoutineEditUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        val dayOfWeek = savedStateHandle.toRoute<TimeRoutineEditScreenDest>().dayOfWeek
        viewModelScope.launch {
            getTimeRoutineUseCase(dayOfWeek).asResultState().collect {
                onCollectedTimeRoutine(it)
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

    private fun onCollectedTimeRoutine(result: ResultState<TimeRoutineComposition?>) {
        viewModelScope.launch {
            when (result) {
                is ResultState.Success -> {
                    val composition = result.data
                    savedStateHandle[SavedStateHandleKeys.ROUTINE_UUID.name] =
                        composition?.timeRoutine?.uuid

                    _uiState.value = TimeRoutineEditUiState.Routine(
                        routineTitle = composition?.timeRoutine?.title ?: "",
                        dayOfWeekList = composition?.dayOfWeeks?.map {
                            it.dayOfWeek
                        } ?: emptyList()
                    )
                }

                is ResultState.Error -> {
                    _uiEvent.emit(
                        TimeRoutineEditUiEvent.ShowAlert(
                            message = Res(id = CommonR.string.message_failed_load_data),
                            confirmIntent = TimeRoutineEditUiIntent.Exit,
                        )
                    )
                }

                is ResultState.Loading -> {
                    _uiState.value = TimeRoutineEditUiState.Loading
                }
            }
        }
    }

    private enum class SavedStateHandleKeys {
        ROUTINE_UUID
    }
}

internal sealed interface TimeRoutineEditUiState {
    data class Routine(
        val routineTitle: String = "",
        val dayOfWeekList: List<DayOfWeek> = emptyList(),
        val routineUuid: String? = null,
        val loading: Boolean = false
    ) : TimeRoutineEditUiState
    data object Loading : TimeRoutineEditUiState
}

internal sealed interface TimeRoutineEditUiIntent {
    data object Save : TimeRoutineEditUiIntent
    data object Cancel : TimeRoutineEditUiIntent
    data object Exit : TimeRoutineEditUiIntent
    data object SaveConfirm : TimeRoutineEditUiIntent
}

internal sealed interface TimeRoutineEditUiEvent {
    data class ShowConfirm(
        val message: UiText,
        val confirmIntent: TimeRoutineEditUiIntent,
        val cancelIntent: TimeRoutineEditUiIntent?,
    ) : TimeRoutineEditUiEvent

    data class ShowAlert(
        val message: UiText,
        val confirmIntent: TimeRoutineEditUiIntent?,
    ) : TimeRoutineEditUiEvent

}
