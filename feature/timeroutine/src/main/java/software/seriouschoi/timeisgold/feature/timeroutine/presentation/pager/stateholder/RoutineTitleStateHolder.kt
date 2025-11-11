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

    fun updateTitle(title: String) {
        _state.update {
            it.copy(title = title)
        }
    }
}