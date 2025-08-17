package software.seriouschoi.timeisgold.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Dest {
    @Serializable data object Splash : Dest
    @Serializable data object Home : Dest
    @Serializable data class SetTimeRoutine(
        val timeRoutineId: String?
    ) : Dest

    @Serializable data object Back : Dest;

}