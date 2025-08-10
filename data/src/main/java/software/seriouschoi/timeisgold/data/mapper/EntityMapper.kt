package software.seriouschoi.timeisgold.data.mapper

import software.seriouschoi.timeisgold.data.database.schema.TimeScheduleDayOfWeekSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeScheduleSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotMemoSchema
import software.seriouschoi.timeisgold.data.database.relations.TimeScheduleRelation
import software.seriouschoi.timeisgold.data.database.relations.TimeScheduleWithDayOfWeeks
import software.seriouschoi.timeisgold.data.database.relations.TimeSlotRelation
import software.seriouschoi.timeisgold.domain.data.timeschedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.data.timeschedule.TimeScheduleDayOfWeekData
import software.seriouschoi.timeisgold.domain.data.timeschedule.TimeScheduleDetailData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotMemoData

internal fun TimeScheduleData.toSchema(id: Long? = null): TimeScheduleSchema {
    return TimeScheduleSchema(
        id = id,
        uuid = this.uuid,
        title = this.timeScheduleName,
        createTime = this.createTime
    )
}

internal fun TimeScheduleDayOfWeekData.toSchema(
    timeScheduleId: Long,
    id: Long? = null
): TimeScheduleDayOfWeekSchema {
    return TimeScheduleDayOfWeekSchema(
        id = id,
        dayOfWeek = this.dayOfWeek,
        uuid = this.uuid,
        timeScheduleId = timeScheduleId
    )
}

internal fun TimeScheduleDayOfWeekSchema.toDomain(): TimeScheduleDayOfWeekData {
    return TimeScheduleDayOfWeekData(
        dayOfWeek = this.dayOfWeek,
        uuid = this.uuid
    )
}

internal fun TimeScheduleRelation.toDomain(): TimeScheduleDetailData {
    val schedule = this.timeSchedule
    val dayOfWeek = this.dayOfWeeks.map {
        it.toDomain()
    }
    val timeSlot = this.timeSlots.map {
        it.toDomain()
    }

    return TimeScheduleDetailData(
        timeScheduleData = TimeScheduleData(
            uuid = schedule.uuid,
            timeScheduleName = schedule.title,
            createTime = schedule.createTime,
            dayOfWeekList = dayOfWeek
        ),
        timeSlotList = timeSlot
    )
}

internal fun TimeSlotSchema.toDomain(): TimeSlotData {
    return TimeSlotData(
        uuid = this.uuid,
        startTime = this.startTime,
        endTime = this.endTime,
        title = this.title,
        createTime = this.createTime
    )
}

internal fun TimeSlotData.toSchema(timeScheduleId: Long, timeSlotId: Long? = null): TimeSlotSchema {
    return TimeSlotSchema(
        uuid = this.uuid,
        startTime = this.startTime,
        endTime = this.endTime,
        title = this.title,
        createTime = this.createTime,
        id = timeSlotId,
        timeScheduleId = timeScheduleId
    )
}

internal fun TimeSlotMemoData.toSchema(
    timeSlotId: Long,
    id: Long? = null
): TimeSlotMemoSchema {
    return TimeSlotMemoSchema(
        uuid = this.uuid,
        memo = this.memo,
        createTime = this.createTime,
        id = id,
        timeSlotId = timeSlotId
    )
}

internal fun TimeSlotMemoSchema.toDomain(): TimeSlotMemoData {
    return TimeSlotMemoData(
        uuid = this.uuid,
        memo = this.memo,
        createTime = this.createTime
    )
}

internal fun TimeSlotRelation.toDomain(): TimeSlotDetailData {
    return TimeSlotDetailData(
        timeSlotData = this.timeSlot.toDomain(),
        timeSlotMemoData = this.memo?.toDomain()
    )
}

internal fun TimeScheduleWithDayOfWeeks.toDomain(): TimeScheduleData {
    return TimeScheduleData(
        uuid = this.timeSchedule.uuid,
        timeScheduleName = this.timeSchedule.title,
        createTime = this.timeSchedule.createTime,
        dayOfWeekList = this.dayOfWeeks.map {
            TimeScheduleDayOfWeekData(
                dayOfWeek = it.dayOfWeek,
                uuid = it.uuid
            )
        }
    )
}