package software.seriouschoi.timeisgold.presentation.feature.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import software.seriouschoi.timeisgold.presentation.navigation.Dest
import software.seriouschoi.timeisgold.presentation.navigation.DestNavigatorPort
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val destNavigatorPort: DestNavigatorPort) : ViewModel() {
    fun openSetTimeRoutine() {
        destNavigatorPort.navigate(Dest.SetTimeRoutine("test_routine_id"))
    }
}