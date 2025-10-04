package software.seriouschoi.timeisgold.feature.timeroutine.timeslots

import android.widget.Toast
import software.seriouschoi.timeisgold.core.common.ui.UiText

/**
 * Created by jhchoi on 2025. 9. 30.
 * jhchoi
 */
internal sealed interface TimeSlotListPageUiEvent {

    data class ShowToast(
        val message: UiText,
        val toastTime: Int = Toast.LENGTH_LONG
    ) : TimeSlotListPageUiEvent
}