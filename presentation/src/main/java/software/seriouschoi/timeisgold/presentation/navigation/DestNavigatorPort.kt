package software.seriouschoi.timeisgold.presentation.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

interface DestNavigatorPort {
    fun setControllerProvider(provider: (() -> NavHostController)?)

    //추상화된 화면 이동.
    fun navigate(
        presentationDest: PresentationDest,
        opts: NavOptionsBuilder.() -> Unit = {}
    )

    fun back()
}