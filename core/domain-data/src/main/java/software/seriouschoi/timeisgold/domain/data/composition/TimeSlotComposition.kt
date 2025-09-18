package software.seriouschoi.timeisgold.domain.data.composition

import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity

@Deprecated("use timeslotEntity")
data class TimeSlotComposition(
    val timeSlotData: TimeSlotEntity,
)