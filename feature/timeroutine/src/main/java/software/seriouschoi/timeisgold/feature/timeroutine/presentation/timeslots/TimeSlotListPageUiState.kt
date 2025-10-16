package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots

import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.list.TimeSlotItemUiState
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

internal sealed interface TimeSlotListPageUiState {
    data class Data(
        val slotItemList: List<TimeSlotItemUiState> = emptyList(),
    ) : TimeSlotListPageUiState {
        companion object {
            fun default(): Data {
                return Data(
                )
            }
        }
    }

    data class Loading(
        val loadingMessage: UiText,
    ) : TimeSlotListPageUiState {
        companion object {
            fun default(): Loading {
                return Loading(
                    UiText.MultipleResArgs.create(
                        CommonR.string.message_format_loading,
                        CommonR.string.text_routine
                    )
                )
            }
        }
    }

    data class Error(
        val errorMessage: UiText,
        val confirmButton: TimeRoutinePageButtonState? = null
    ) : TimeSlotListPageUiState
}

internal data class TimeRoutinePageButtonState(
    val buttonLabel: UiText,
    val intent: TimeRoutinePageUiIntent,
)