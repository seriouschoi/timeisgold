package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.core.common.util.LocalTimeUtil
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 2.
 * jhchoi
 */
class NormalizeMinutesForUiUseCase @Inject constructor(
    private val timeSlotPolicy: TimeSlotPolicy
) {
    fun invoke(minutesOfDay: Int) : Int{
        return LocalTimeUtil.normalize(minutesOfDay, timeSlotPolicy.timeSlotDragStep)
    }
}