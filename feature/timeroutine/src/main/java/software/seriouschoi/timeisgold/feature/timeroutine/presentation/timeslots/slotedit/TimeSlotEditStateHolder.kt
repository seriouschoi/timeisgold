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
            is TimeSlotEditStateIntent.UpdateTitle -> {
                _state.update {
                    it?.copy(
                        title = intent.title
                    )
                }
            }
            is TimeSlotEditStateIntent.Init -> {
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

            TimeSlotEditStateIntent.Clear -> {
                _state.update { null }
            }

        }
    }
}

internal sealed interface TimeSlotEditStateIntent {
    data class UpdateTitle(
        val title: String
    ) : TimeSlotEditStateIntent

    data class Init(
        val slotId: String? = null,
        val slotTitle: String? = null,
        val startTime: LocalTime? = null,
        val endTime: LocalTime? = null,
    ) : TimeSlotEditStateIntent

    object Clear: TimeSlotEditStateIntent
}

internal data class TimeSlotEditState(
    val slotUuid: String? = null,
    val title: String = "",
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
)
