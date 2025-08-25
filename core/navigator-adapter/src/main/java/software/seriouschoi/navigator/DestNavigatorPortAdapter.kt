package software.seriouschoi.navigator

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
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
        presentationDest: NavigatorDest,
        opts: NavOptionsBuilder.() -> Unit
    ) {
        val navController = provider?.invoke()
        navController?.navigate(presentationDest) {
            opts(this)
        }
    }

    override fun back() {
        val navController = provider?.invoke()
        navController?.popBackStack()
    }
}