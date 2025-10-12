package software.seriouschoi.timeisgold.feature.timeroutine.data

import kotlinx.coroutines.flow.MutableStateFlow
import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 10. 4.
 * jhchoi
 */
internal class TimeRoutineFeatureState(){
    val data = MutableStateFlow(TimeRoutineFeatureStateData(DayOfWeek.MONDAY))
}