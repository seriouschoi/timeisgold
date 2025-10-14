package software.seriouschoi.timeisgold.feature.timeroutine.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Created by jhchoi on 2025. 10. 4.
 * jhchoi
 */
internal class TimeRoutineFeatureState(){
    val data = MutableStateFlow(TimeRoutineFeatureStateData(defaultDayOfWeek))

    companion object {
        private val defaultDayOfWeek
            get() = DayOfWeek.from(LocalDate.now())
    }
}