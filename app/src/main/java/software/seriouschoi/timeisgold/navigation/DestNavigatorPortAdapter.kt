package software.seriouschoi.timeisgold.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import software.seriouschoi.timeisgold.presentation.navigation.PresentationDest
import software.seriouschoi.timeisgold.presentation.navigation.DestNavigatorPort
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DestNavigatorPortAdapter @Inject constructor(
) : DestNavigatorPort {

    private var provider: (() -> NavHostController)? = null

    override fun setControllerProvider(provider: (() -> NavHostController)?) {
        this.provider = provider
    }

    override fun navigate(
        to: PresentationDest,
        opts: NavOptionsBuilder.() -> Unit
    ) {
        val navController = provider?.invoke()
        navController?.navigate(to) {
            opts(this)
        }
    }

    override fun back() {
        val navController = provider?.invoke()
        navController?.popBackStack()
    }
}