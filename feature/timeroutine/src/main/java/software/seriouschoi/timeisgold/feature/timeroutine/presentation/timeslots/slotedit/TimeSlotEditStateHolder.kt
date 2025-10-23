package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime
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
    
    fun sendIntent(intent: TimeSlotEditStateIntent) {
        when (intent) {
            is TimeSlotEditStateIntent.Update -> {
                _state.update { it: TimeSlotEditState? ->
                    val newState = it ?: TimeSlotEditState()
                    newState.copy(
                        slotUuid = intent.slotId,
                        title = intent.slotTitle ?: "",
                        startTime = intent.startTime,
                        endTime = intent.endTime,
                    )
                }
            }
        }
    }
}

internal sealed interface TimeSlotEditStateIntent {
    data class Update(
        val slotId: String? = null,
        val slotTitle: String? = null,
        val startTime: LocalTime? = null,
        val endTime: LocalTime? = null,
    ) : TimeSlotEditStateIntent
}