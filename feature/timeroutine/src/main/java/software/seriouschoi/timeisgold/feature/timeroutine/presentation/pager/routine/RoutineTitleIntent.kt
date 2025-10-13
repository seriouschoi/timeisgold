package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.routine

/**
 * Created by jhchoi on 2025. 10. 10.
 * jhchoi
 */
internal sealed interface RoutineTitleIntent {
    data class EditTitle(val title: String) : RoutineTitleIntent
}
