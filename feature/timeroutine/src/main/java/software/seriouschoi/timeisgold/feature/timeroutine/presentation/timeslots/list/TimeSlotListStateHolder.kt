package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import javax.inject.Inject

internal class TimeSlotListStateHolder @Inject constructor(
) {
    private val _state = MutableStateFlow(TimeSlotListState())

    val state: StateFlow<TimeSlotListState> = _state

    fun sendIntent(intent: TimeSlotListStateIntent) {
        when (intent) {
            TimeSlotListStateIntent.Loading -> {
                _state.update {
                    it.loadingState()
                }
            }

            is TimeSlotListStateIntent.Error -> {
                _state.update {
                    it.copy(
                        loadingMessage = null,
                        errorMessage = intent.message
                    )
                }
            }

            is TimeSlotListStateIntent.UpdateList -> {
                _state.update {
                    it.copy(
                        loadingMessage = null,
                        errorMessage = null,
                        slotItemList = intent.itemList
                    )
                }
            }
        }
    }
}

@Deprecated("Deprecated use simple method")
internal sealed interface TimeSlotListStateIntent {
    data class Error(val message: UiText) : TimeSlotListStateIntent
    data class UpdateList(val itemList: List<TimeSlotItemUiState>) : TimeSlotListStateIntent
    object Loading : TimeSlotListStateIntent
}