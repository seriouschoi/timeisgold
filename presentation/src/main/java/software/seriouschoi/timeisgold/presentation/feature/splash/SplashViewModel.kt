package software.seriouschoi.timeisgold.presentation.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.presentation.feature.home.HomePresentationRoute
import javax.inject.Inject

@HiltViewModel
internal class SplashViewModel @Inject constructor(
    private val destNavigatorPort: DestNavigatorPort,
) : ViewModel() {

    fun onLaunch() = viewModelScope.launch {
        destNavigatorPort.navigate(HomePresentationRoute) {
            this.popUpTo(SplashPresentationDest)
            this.launchSingleTop = true
        }
    }
}