package software.seriouschoi.timeisgold.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import software.seriouschoi.timeisgold.presentation.feature.home.HomePresentationDest
import software.seriouschoi.timeisgold.presentation.feature.home.home
import software.seriouschoi.timeisgold.presentation.feature.splash.splash
import software.seriouschoi.timeisgold.presentation.feature.timeroutine.set.setTimeRoutine

/**
 * Created by jhchoi on 2025. 8. 19.
 * jhchoi@neofect.com
 */
@Serializable
data object PresentationRoot

fun NavGraphBuilder.presentationSection() {
    navigation<PresentationRoot>(
        startDestination = HomePresentationDest,
    ) {
        splash()
        home()
        setTimeRoutine()
    }
}