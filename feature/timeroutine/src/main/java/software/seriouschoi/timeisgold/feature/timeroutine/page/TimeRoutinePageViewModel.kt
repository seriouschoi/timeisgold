package software.seriouschoi.timeisgold.feature.timeroutine.page

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.core.common.ui.ResultState
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.core.common.ui.asResultState
import software.seriouschoi.timeisgold.core.common.util.Envelope
import software.seriouschoi.timeisgold.core.domain.mapper.onlySuccess
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchTimeRoutineCompositionUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeslot.WatchTimeSlotUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.edit.TimeRoutineEditScreenRoute
import software.seriouschoi.timeisgold.feature.timeroutine.timeslot.edit.TimeSlotEditScreenRoute
import java.io.Serializable
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
    private val watchTimeRoutineCompositionUseCase: WatchTimeRoutineCompositionUseCase,
    private val navigator: DestNavigatorPort,
    private val savedStateHandle: SavedStateHandle,
    private val setTimeSlotUseCase: SetTimeSlotUseCase,
    private val watchTimeSlotUseCase: WatchTimeSlotUseCase
) : ViewModel() {

    // TODO: jhchoi 2025. 9. 19. 현재 뷰모델의 루틴 읽어오는 파이프라인 만들기. 요일 파이프라인을 만들어서 그걸 수신하자.

    // TODO: jhchoi 2025. 9. 19. 이건 지우자.
    private data class ViewModelData(
        val dayOfWeekOrdinal: Int,
    ) : Serializable

    // TODO: jhchoi 2025. 9. 19. 아래의 flow는 파이프라인으로 정리. 
    private val _uiState = MutableStateFlow<TimeRoutinePageUiState>(
        TimeRoutinePageUiState.Loading(
            UiText.MultipleResArgs.create(
                CommonR.string.message_format_loading,
                CommonR.string.text_routine
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _intent = MutableSharedFlow<Envelope<TimeRoutinePageUiIntent>>()

    // TODO: jhchoi 2025. 9. 19. 이건 지우자.
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
            watchTimeRoutineCompositionUseCase(dayOfWeek).asResultState().collect { resultState ->
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
                    loadingMessage = UiText.MultipleResArgs.create(
                        CommonR.string.message_format_loading,
                        CommonR.string.text_routine
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
                        CommonR.string.message_format_routine_create_confirm,
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
                    CommonR.string.message_format_routine_create_confirm,
                    dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
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
                    uuid = it.uuid,
                    title = it.title,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    slotClickIntent = TimeRoutinePageUiIntent.ShowSlotEdit(
                        it.uuid, routineComposition.timeRoutine.uuid
                    )
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

            is TimeRoutinePageUiIntent.ShowSlotEdit -> {
                val route = TimeSlotEditScreenRoute(
                    timeSlotUuid = intent.slotId,
                    timeRoutineUuid = intent.routineId
                )
                navigator.navigate(route)
            }

            is TimeRoutinePageUiIntent.UpdateSlot -> {
                updateTimeSlot(intent)
            }
        }
    }

    private fun updateTimeSlot(intent: TimeRoutinePageUiIntent.UpdateSlot) {
        viewModelScope.launch {
            val dayOfWeek = viewModelData?.dayOfWeekOrdinal?.let {
                DayOfWeek.entries.getOrNull(it)
            } ?: return@launch
            val routine =
                watchTimeRoutineCompositionUseCase.invoke(dayOfWeek).asResultState().onlySuccess()
                    .first() ?: return@launch

            val timeSlot =
                watchTimeSlotUseCase.invoke(intent.uuid).asResultState().onlySuccess().first()
                    ?: return@launch

            val newTimeSlot = timeSlot.copy(
                startTime = intent.newStart,
                endTime = intent.newEnd
            )

            setTimeSlotUseCase.invoke(
                timeRoutineUuid = routine.timeRoutine.uuid,
                timeSlotData = newTimeSlot
            )
        }
    }

    fun sendIntent(createRoutine: TimeRoutinePageUiIntent) {
        viewModelScope.launch {
            _intent.emit(Envelope(createRoutine))
        }
    }
}
