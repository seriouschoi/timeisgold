package software.seriouschoi.timeisgold.feature.timeroutine.fake

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.navigator.NavigatorRoute

/**
 * Created by jhchoi on 2025. 9. 4.
 * jhchoi
 */
object FakeDestNavigatorPortAdapter : DestNavigatorPort {
    override fun setControllerProvider(provider: (() -> NavHostController)?) {
        TODO("Not yet implemented")
    }

    override fun navigate(
        route: NavigatorRoute,
        opts: NavOptionsBuilder.() -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun back() {
        TODO("Not yet implemented")
    }
}