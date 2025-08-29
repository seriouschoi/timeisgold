package software.seriouschoi.timeisgold.data.mapper

import software.seriouschoi.timeisgold.data.database.view.TimeRoutineJoinTimeSlotView
import software.seriouschoi.timeisgold.domain.entities.TimeSlotEntity

internal fun TimeRoutineJoinTimeSlotView.toTimeSlotEntity(): TimeSlotEntity {
    return TimeSlotEntity(
        uuid = this.timeSlotUuid,
        startTime = this.timeSlotStartTime,
        endTime = this.timeSlotEndTime,
        title = this.timeSlotTitle,
        createTime = this.timeSlotCreateTime
    )
}