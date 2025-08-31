package software.seriouschoi.timeisgold.feature.timeroutine.bar.timeroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.domain.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineUseCase
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeRoutinePageViewModel @Inject constructor(
    val getTimeRoutineUseCase: GetTimeRoutineUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<TimeRoutineUiState>(TimeRoutineUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _intent = MutableSharedFlow<TimeRoutineIntent>()

    init {
        viewModelScope.launch {
            _intent.collect {
                onCollectedIntent(it)
            }
        }
    }

    fun load(dayOfWeek: DayOfWeek) {
        viewModelScope.launch {
            getTimeRoutineUseCase(dayOfWeek).asResultState().collect {
                onCollectedTimeRoutineComposition(it)
            }
        }
    }

    private fun onCollectedIntent(intent: TimeRoutineIntent) {
        when(intent) {
            is TimeRoutineIntent.CreateRoutine -> {
                // TODO: show routine create page.
            }
        }
    }

    private fun onCollectedTimeRoutineComposition(result: ResultState<TimeRoutineComposition?>) {
        when (result) {
            is ResultState.Loading -> {
                _uiState.value = TimeRoutineUiState.Loading
            }

            is ResultState.Success -> {
                val data = result.data
                if (data == null) {
                    _uiState.value = TimeRoutineUiState.Empty
                } else {
                    _uiState.value = TimeRoutineUiState.Routine(
                        title = data.timeRoutine.title,
                        slotItemList = data.timeSlots.map {
                            TimeSlotItemUiState(
                                title = it.title,
                                startTime = it.startTime,
                                endTime = it.endTime
                            )
                        },
                        dayOfWeeks = data.dayOfWeeks.map {
                            it.dayOfWeek
                        }.sorted()
                    )
                }
            }

            is ResultState.Error -> {
                _uiState.value = TimeRoutineUiState.Error
            }
        }
    }

    fun sendIntent(createRoutine: TimeRoutineIntent.CreateRoutine) {
        viewModelScope.launch {
            _intent.emit(createRoutine)
        }
    }
}

internal sealed interface TimeRoutineUiState {
    data class Routine(
        val title: String,
        val slotItemList: List<TimeSlotItemUiState> = emptyList(),
        val dayOfWeeks: List<DayOfWeek> = listOf(),
    ) : TimeRoutineUiState

    object Empty : TimeRoutineUiState
    object Loading : TimeRoutineUiState
    object Error : TimeRoutineUiState
}

internal data class TimeSlotItemUiState(
    val title: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
)

internal sealed interface TimeRoutineIntent {
    data class CreateRoutine(val dayOfWeek: DayOfWeek) : TimeRoutineIntent

}
