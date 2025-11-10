package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 14.
 * jhchoi
 */
internal class RoutineTitleStateHolder @Inject constructor() {
    private val _state = MutableStateFlow(RoutineTitleState())
    val state: StateFlow<RoutineTitleState> = _state

    fun sendIntent(intent: RoutineTitleIntent) {
        when(intent) {
            is RoutineTitleIntent.Update -> {
                _state.update {
                    it.copy(title = intent.title)
                }
            }
        }
    }
}

internal data class RoutineTitleState(
    val title: String = ""
)

internal sealed interface RoutineTitleIntent {
    data class Update(
        val title: String
    ) : RoutineTitleIntent
}