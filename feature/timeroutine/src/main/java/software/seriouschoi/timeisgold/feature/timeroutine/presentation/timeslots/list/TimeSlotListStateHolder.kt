package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.item.TimeSlotItemUiState
import javax.inject.Inject

internal class TimeSlotListStateHolder @Inject constructor(
) {
    private val _state = MutableStateFlow(TimeSlotListState())

    val state: StateFlow<TimeSlotListState> = _state

    fun setList(
        itemList: List<TimeSlotItemUiState>
    ) {
        _state.update {
            TimeSlotListState(
                slotItemList = itemList
            )
        }
    }

    fun showLoading() {
        _state.update {
            it.copy(
                loadingMessage = UiText.MultipleResArgs.create(
                    CommonR.string.message_format_loading,
                    CommonR.string.text_routine
                ),
                errorMessage = null
            )
        }
    }

    fun showError(errorMessage: UiText) {
        _state.update {
            TimeSlotListState(
                errorMessage = errorMessage,
            )
        }
    }
}