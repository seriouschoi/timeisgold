package software.seriouschoi.timeisgold.data.mapper

import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineEntity

internal fun TimeRoutineEntity.toTimeRoutineSchemaSchema(id:Long?): TimeRoutineSchema {
    return TimeRoutineSchema(
        uuid = this.uuid,
        title = this.title,
        createTime = this.createTime,
        id = id
    )
}

internal fun TimeRoutineSchema.schemaToTimeRoutineEntity(): TimeRoutineEntity {
    return TimeRoutineEntity(
        uuid = this.uuid,
        title = this.title,
        createTime = this.createTime
    )
}