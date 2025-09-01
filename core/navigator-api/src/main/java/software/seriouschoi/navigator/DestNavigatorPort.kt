package software.seriouschoi.navigator

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

interface DestNavigatorPort {
    fun setControllerProvider(provider: (() -> NavHostController)?)

    //추상화된 화면 이동.
    fun navigate(
        route: NavigatorRoute,
        opts: NavOptionsBuilder.() -> Unit = {}
    )

    fun back()
}