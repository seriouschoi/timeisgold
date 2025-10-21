package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 21.
 * jhchoi
 */
internal class TimeSlotEditStateHolder @Inject constructor() {
    private val _state = MutableStateFlow<TimeSlotEditState?>(
        null
    )
    val state: StateFlow<TimeSlotEditState?> = _state

}