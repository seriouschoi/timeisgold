package software.seriouschoi.timeisgold.data.mapper

import software.seriouschoi.timeisgold.data.database.schema.view.TimeRoutineJoinDayOfWeekView
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineEntity

internal fun TimeRoutineJoinDayOfWeekView.toTimeRoutineEntity() : TimeRoutineEntity {
    return TimeRoutineEntity(
        title = this.routineName,
        uuid = this.routineUuid,
        createTime = this.routineCreateTime
    )
}