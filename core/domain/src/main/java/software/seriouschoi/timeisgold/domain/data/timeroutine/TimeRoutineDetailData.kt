package software.seriouschoi.timeisgold.domain.data.timeroutine

import software.seriouschoi.timeisgold.domain.entities.TimeSlotEntity

@Deprecated("use composition")
data class TimeRoutineDetailData(
    val timeRoutineData: TimeRoutineData,
    val timeSlotList: List<TimeSlotEntity>,
)