package software.seriouschoi.timeisgold.data.mapper

import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotMemoSchema
import software.seriouschoi.timeisgold.data.database.relations.TimeRoutineRelation
import software.seriouschoi.timeisgold.data.database.relations.TimeRoutineWithDayOfWeeks
import software.seriouschoi.timeisgold.data.database.relations.TimeSlotRelation
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineData
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDayOfWeekData
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDetailData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotMemoData

internal fun TimeRoutineData.toSchema(id: Long? = null): TimeRoutineSchema {
    return TimeRoutineSchema(
        id = id,
        uuid = this.uuid,
        title = this.title,
        createTime = this.createTime
    )
}

internal fun TimeRoutineDayOfWeekData.toSchema(
    timeRoutineId: Long,
    id: Long? = null
): TimeRoutineDayOfWeekSchema {
    return TimeRoutineDayOfWeekSchema(
        id = id,
        dayOfWeek = this.dayOfWeek,
        uuid = this.uuid,
        timeRoutineId = timeRoutineId
    )
}

internal fun TimeRoutineDayOfWeekSchema.toDomain(): TimeRoutineDayOfWeekData {
    return TimeRoutineDayOfWeekData(
        dayOfWeek = this.dayOfWeek,
        uuid = this.uuid
    )
}

internal fun TimeRoutineRelation.toDomain(): TimeRoutineDetailData {
    val routine = this.timeRoutine
    val dayOfWeek = this.dayOfWeeks.map {
        it.toDomain()
    }.sortedBy {
        it.dayOfWeek
    }
    val timeSlot = this.timeSlots.map {
        it.toDomain()
    }.sortedBy {
        it.startTime
    }

    return TimeRoutineDetailData(
        timeRoutineData = TimeRoutineData(
            uuid = routine.uuid,
            title = routine.title,
            createTime = routine.createTime,
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

internal fun TimeSlotData.toSchema(timeRoutineId: Long, timeSlotId: Long? = null): TimeSlotSchema {
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

internal fun TimeRoutineWithDayOfWeeks.toDomain(): TimeRoutineData {
    return TimeRoutineData(
        uuid = this.timeRoutine.uuid,
        title = this.timeRoutine.title,
        createTime = this.timeRoutine.createTime,
        dayOfWeekList = this.dayOfWeeks.map {
            TimeRoutineDayOfWeekData(
                dayOfWeek = it.dayOfWeek,
                uuid = it.uuid
            )
        }
    )
}