package software.seriouschoi.timeisgold.data.mapper

import software.seriouschoi.timeisgold.data.database.view.TimeRoutineJoinDayOfWeekView
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity

internal fun TimeRoutineJoinDayOfWeekView.toTimeRoutineEntity() : TimeRoutineEntity {
    return TimeRoutineEntity(
        title = this.routineName,
        uuid = this.routineUuid,
        createTime = this.routineCreateTime
    )
}

internal fun TimeRoutineJoinDayOfWeekView.toTimeRoutineDayOfWeekEntity() : TimeRoutineDayOfWeekEntity {
    return TimeRoutineDayOfWeekEntity(
        dayOfWeek = this.dayOfWeek
    )
}
