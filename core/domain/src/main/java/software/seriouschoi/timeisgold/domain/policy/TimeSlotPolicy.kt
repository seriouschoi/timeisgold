package software.seriouschoi.timeisgold.domain.policy

import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 1.
 * jhchoi
 */
class TimeSlotPolicy @Inject constructor() {
    val titleLengthRange = 1..15
    val timeSlotDragStep = 15
}