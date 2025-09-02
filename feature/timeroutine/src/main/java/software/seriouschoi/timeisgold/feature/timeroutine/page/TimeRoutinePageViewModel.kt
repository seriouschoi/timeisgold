package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditScreenRoute
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
    val navigator: DestNavigatorPort,
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
                when (it) {
                    is ResultState.Loading -> {
                        _uiState.value = TimeRoutineUiState.Loading
                    }

                    is ResultState.Success -> {
                        onCollectedTimeRoutine(it.data)
                    }

                    is ResultState.Error -> {
                        _uiState.value = TimeRoutineUiState.Error
                    }
                }
            }
        }
    }

    private fun onCollectedIntent(intent: TimeRoutineIntent) {
        when (intent) {
            is TimeRoutineIntent.CreateRoutine -> {
                navigator.navigate(TimeRoutineEditScreenRoute(intent.dayOfWeek))
            }
        }
    }

    private fun onCollectedTimeRoutine(domainResult: DomainResult<TimeRoutineComposition>) {
        _uiState.value = when (domainResult) {
            is DomainResult.Failure -> {
                TimeRoutineUiState.Empty
            }

            is DomainResult.Success -> {
                val data = domainResult.value
                TimeRoutineUiState.Routine(
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
    }

    fun sendIntent(createRoutine: TimeRoutineIntent.CreateRoutine) {
        viewModelScope.launch {
            _intent.emit(createRoutine)
        }
    }
}

internal sealed interface TimeRoutineUiState {
    data class Routine(
        val title: String = "",
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
