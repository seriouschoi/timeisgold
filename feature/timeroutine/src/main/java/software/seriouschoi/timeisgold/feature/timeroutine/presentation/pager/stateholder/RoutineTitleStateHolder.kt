package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import software.seriouschoi.timeisgold.core.common.ui.UiText
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 14.
 * jhchoi
 */
internal class RoutineTitleStateHolder @Inject constructor() {
    private val _state = MutableStateFlow(RoutineTitleState())
    val state: StateFlow<RoutineTitleState> = _state

    fun update(intent: RoutineTitleIntent) {
        when(intent) {
            is RoutineTitleIntent.Update -> {
                _state.update {
                    it.copy(title = UiText.Raw(intent.title))
                }
            }
        }
    }
}

internal data class RoutineTitleState(
    val title: UiText = UiText.Raw("")
)

internal sealed interface RoutineTitleIntent {
    data class Update(
        val title: String
    ) : RoutineTitleIntent
}