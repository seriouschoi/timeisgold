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
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineCompositionUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditScreenRoute
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeRoutinePageViewModel @Inject constructor(
    val getTimeRoutineCompositionUseCase: GetTimeRoutineCompositionUseCase,
    val navigator: DestNavigatorPort,
) : ViewModel() {
    private val _uiState = MutableStateFlow<TimeRoutinePageUiState>(TimeRoutinePageUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _intent = MutableSharedFlow<TimeRoutinePageUiIntent>()

    init {
        viewModelScope.launch {
            _intent.collect {
                onCollectedIntent(it)
            }
        }
    }

    fun load(dayOfWeek: DayOfWeek) {
        viewModelScope.launch {
            getTimeRoutineCompositionUseCase(dayOfWeek).asResultState().collect {
                when (it) {
                    is ResultState.Loading -> {
                        _uiState.value = TimeRoutinePageUiState.Loading
                    }

                    is ResultState.Success -> {
                        onCollectedTimeRoutine(it.data)
                    }

                    is ResultState.Error -> {
                        _uiState.value = TimeRoutinePageUiState.Error
                    }
                }
            }
        }
    }

    private fun onCollectedIntent(intent: TimeRoutinePageUiIntent) {
        when (intent) {
            is TimeRoutinePageUiIntent.CreateRoutine -> {
                navigator.navigate(TimeRoutineEditScreenRoute(intent.dayOfWeek.ordinal))
            }
        }
    }

    private fun onCollectedTimeRoutine(domainResult: DomainResult<TimeRoutineComposition>) {
        _uiState.value = when (domainResult) {
            is DomainResult.Failure -> {
                TimeRoutinePageUiState.Empty
            }

            is DomainResult.Success -> {
                val data = domainResult.value
                TimeRoutinePageUiState.Routine(
                    title = data.timeRoutine.title,
                    slotItemList = data.timeSlots.map {
                        TimeRoutinePageSlotItemUiState(
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

    fun sendIntent(createRoutine: TimeRoutinePageUiIntent.CreateRoutine) {
        viewModelScope.launch {
            _intent.emit(createRoutine)
        }
    }
}
