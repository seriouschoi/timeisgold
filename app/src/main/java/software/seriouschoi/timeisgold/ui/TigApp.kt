package software.seriouschoi.timeisgold.ui

import androidx.compose.runtime.Composable
import software.seriouschoi.timeisgold.core.common.ui.TigTheme
import software.seriouschoi.timeisgold.core.common.ui.components.TigScaffold
import software.seriouschoi.timeisgold.navigation.ui.AppNavHost

/**
 * Created by jhchoi on 2025. 8. 20.
 * jhchoi
 */
@Composable
fun TigApp() {
    TigTheme {
        TigScaffold(
            content = { inner ->
                AppNavHost()
            },
        )
    }
}