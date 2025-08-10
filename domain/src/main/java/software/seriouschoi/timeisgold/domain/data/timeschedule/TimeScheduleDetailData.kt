package software.seriouschoi.timeisgold.domain.data.timeschedule

import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotData

data class TimeScheduleDetailData(
    val timeScheduleData: TimeScheduleData,
    val timeSlotList: List<TimeSlotData>,
)