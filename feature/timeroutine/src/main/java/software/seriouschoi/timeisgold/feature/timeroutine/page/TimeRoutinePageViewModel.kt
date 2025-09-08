package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.GetTimeRoutineCompositionUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.bar.R
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditScreenRoute
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 8. 26.
 * jhchoi
 */
@HiltViewModel
internal class TimeRoutinePageViewModel @Inject constructor(
    val getTimeRoutineCompositionUseCase: GetTimeRoutineCompositionUseCase,
    val navigator: DestNavigatorPort,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private data class ViewModelData(
        val dayOfWeekOrdinal: Int,
    ) : java.io.Serializable

    private val _uiState = MutableStateFlow<TimeRoutinePageUiState>(
        TimeRoutinePageUiState.Loading(
            UiText.Res.create(CommonR.string.message_loading)
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _intent = MutableSharedFlow< Envelope<TimeRoutinePageUiIntent>>()

    private val viewModelData
        get() = savedStateHandle.get<ViewModelData>("data")

    init {
        viewModelScope.launch {
            _intent.collect {
                handleIntentSideEffect(it.payload)
            }
        }
    }

    fun load(dayOfWeek: DayOfWeek) {
        viewModelScope.launch {
            savedStateHandle["data"] = ViewModelData(
                dayOfWeekOrdinal = dayOfWeek.ordinal
            )
            getTimeRoutineCompositionUseCase(dayOfWeek).asResultState().collect { resultState ->
                _uiState.update {
                    it.reduceResultState(resultState, dayOfWeek)
                }
            }
        }
    }

    private fun TimeRoutinePageUiState.reduceResultState(
        resultState: ResultState<DomainResult<TimeRoutineComposition>>,
        dayOfWeek: DayOfWeek,
    ): TimeRoutinePageUiState {
        val dayOfWeekName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        when (resultState) {
            ResultState.Loading -> {
                return TimeRoutinePageUiState.Loading(
                    loadingMessage = UiText.Res.create(
                        R.string.message_routine_loading,
                        dayOfWeekName
                    )
                )
            }

            is ResultState.Success -> {
                val data = resultState.data
                return reduceDomainResult(data, dayOfWeek)
            }

            is ResultState.Error -> {
                return TimeRoutinePageUiState.Error(
                    errorMessage = UiText.Res.create(
                        R.string.message_routine_create_confirm,
                        dayOfWeekName
                    )
                )
            }
        }
    }

    private fun TimeRoutinePageUiState.reduceDomainResult(
        data: DomainResult<TimeRoutineComposition>,
        dayOfWeek: DayOfWeek,
    ): TimeRoutinePageUiState {
        when (data) {
            is DomainResult.Failure -> return TimeRoutinePageUiState.Empty(
                emptyMessage = UiText.Res.create(
                    R.string.message_routine_create_confirm,
                    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                )
            )

            is DomainResult.Success -> {
                val routineComposition = data.value
                return this.reduceTimeRoutineComposition(routineComposition, dayOfWeek)
            }
        }
    }

    private fun TimeRoutinePageUiState.reduceTimeRoutineComposition(
        routineComposition: TimeRoutineComposition,
        dayOfWeek: DayOfWeek,
    ): TimeRoutinePageUiState {
        return TimeRoutinePageUiState.Routine(
            title = routineComposition.timeRoutine.title,
            slotItemList = routineComposition.timeSlots.map {
                TimeRoutinePageSlotItemUiState(
                    title = it.title,
                    startTime = it.startTime,
                    endTime = it.endTime
                )
            },
            dayOfWeeks = routineComposition.dayOfWeeks.map {
                it.dayOfWeek
            }.sorted(),
            dayOfWeekName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        )
    }

    private fun handleIntentSideEffect(intent: TimeRoutinePageUiIntent) {
        when (intent) {
            is TimeRoutinePageUiIntent.ModifyRoutine,
            is TimeRoutinePageUiIntent.CreateRoutine -> {
                val dayOfWeekOrdinal = viewModelData?.dayOfWeekOrdinal
                if (dayOfWeekOrdinal != null)
                    navigator.navigate(TimeRoutineEditScreenRoute(dayOfWeekOrdinal))
            }

        }
    }

    fun sendIntent(createRoutine: TimeRoutinePageUiIntent) {
        viewModelScope.launch {
            _intent.emit(Envelope(createRoutine))
        }
    }
}
