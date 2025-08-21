package software.seriouschoi.timeisgold.navigation.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import software.seriouschoi.navigator.DestNavigatorPort
import javax.inject.Inject

@HiltViewModel
class AppNavHostViewModel @Inject constructor(
    val destNavigatorPort: DestNavigatorPort,
) : ViewModel()