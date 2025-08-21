package software.seriouschoi.timeisgold.presentation.feature.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.timeisgold.presentation.feature.timeroutine.set.SetTimeRoutinePresentationDest
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(private val destNavigatorPort: DestNavigatorPort) :
    ViewModel() {
    fun openSetTimeRoutine() {
        destNavigatorPort.navigate(SetTimeRoutinePresentationDest("test_routine_id"))
    }
}