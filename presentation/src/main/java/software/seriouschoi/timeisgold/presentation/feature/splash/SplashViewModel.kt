package software.seriouschoi.timeisgold.presentation.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import software.seriouschoi.timeisgold.presentation.navigation.Dest
import software.seriouschoi.timeisgold.presentation.navigation.DestNavigatorPort
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val destNavigatorPort: DestNavigatorPort,
) : ViewModel() {

    fun onLaunch() = viewModelScope.launch {
        delay(500) // TODO: demo.
        destNavigatorPort.navigate(Dest.Home) {
            this.popUpTo(Dest.Splash)
            this.launchSingleTop = true
        }
    }
}