package software.seriouschoi.timeisgold.feature.timeroutine.page

import android.widget.Toast
import software.seriouschoi.timeisgold.core.common.ui.UiText

/**
 * Created by jhchoi on 2025. 9. 30.
 * jhchoi
 */
internal sealed interface TimeRoutinePageUiEvent {
    data class TimeSlotDragCursorRefresh(
        val cursorSlotItem: TimeSlotItemUiState
    ) : TimeRoutinePageUiEvent

    data class ShowToast(
        val message: UiText,
        val toastTime: Int = Toast.LENGTH_LONG
    ) : TimeRoutinePageUiEvent
}