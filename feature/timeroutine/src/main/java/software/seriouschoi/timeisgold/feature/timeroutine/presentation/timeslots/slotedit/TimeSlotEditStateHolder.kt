package software.seriouschoi.timeisgold.feature.timeroutine.presentation.timeslots.slotedit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
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
            is TimeSlotEditStateIntent.Init -> {
                _state.update {
                    intent.state
                }
            }

            is TimeSlotEditStateIntent.Update -> {
                _state.update {
                    val newState = it?.copy(
                        slotUuid = intent.slotId ?: it.slotUuid,
                        title = intent.slotTitle ?: it.title,
                        startTime = intent.startTime ?: it.startTime,
                        endTime = intent.endTime ?: it.endTime
                    )
                    Timber.d("update intent. intent=$intent, newState=$newState")
                    newState
                }
            }
        }
    }
}

internal sealed interface TimeSlotEditStateIntent {
    data class Init(
        val state: TimeSlotEditState?
    ) : TimeSlotEditStateIntent

    data class Update(
        val slotId: String? = null,
        val slotTitle: String? = null,
        val startTime: LocalTime? = null,
        val endTime: LocalTime? = null
    ) : TimeSlotEditStateIntent
}

internal data class TimeSlotEditState(
    val slotUuid: String? = null,
    val title: String,
    val startTime: LocalTime,
    val endTime: LocalTime
)
