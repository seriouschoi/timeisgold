package software.seriouschoi.timeisgold.ui

import androidx.compose.runtime.Composable
import software.seriouschoi.timeisgold.navigation.ui.AppNavHost

/**
 * Created by jhchoi on 2025. 8. 20.
 * jhchoi
 */
@Composable
fun TigApp(

) {
    AppTheme {
        AppScaffold(
            content = { inner ->
                AppNavHost()
            }
        )
    }
}