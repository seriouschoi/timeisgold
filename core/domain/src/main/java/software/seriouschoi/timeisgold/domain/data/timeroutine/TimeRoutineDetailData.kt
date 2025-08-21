package software.seriouschoi.timeisgold.domain.data.timeroutine

import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotData

data class TimeRoutineDetailData(
    val timeRoutineData: TimeRoutineData,
    val timeSlotList: List<TimeSlotData>,
)