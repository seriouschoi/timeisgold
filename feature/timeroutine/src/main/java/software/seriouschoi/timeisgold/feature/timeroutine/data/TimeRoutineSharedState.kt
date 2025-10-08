package software.seriouschoi.timeisgold.feature.timeroutine.data

import kotlinx.coroutines.flow.MutableStateFlow
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 4.
 * jhchoi
 */
internal class TimeRoutineSharedState @Inject constructor(){
    val state = MutableStateFlow(TimeRoutineSharedStateData(DayOfWeek.MONDAY))
}