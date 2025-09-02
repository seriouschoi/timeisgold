package software.seriouschoi.timeisgold.data.mapper

import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity

internal fun TimeSlotEntity.toTimeSlotSchema(
    timeRoutineId: Long,
    timeSlotId: Long? = null,
): TimeSlotSchema {
    return TimeSlotSchema(
        uuid = this.uuid,
        startTime = this.startTime,
        endTime = this.endTime,
        title = this.title,
        createTime = this.createTime,
        id = timeSlotId,
        timeRoutineId = timeRoutineId
    )
}

internal fun TimeSlotSchema.toTimeSlotEntity(): TimeSlotEntity {
    return TimeSlotEntity(
        uuid = this.uuid,
        startTime = this.startTime,
        endTime = this.endTime,
        title = this.title,
        createTime = this.createTime
    )
}