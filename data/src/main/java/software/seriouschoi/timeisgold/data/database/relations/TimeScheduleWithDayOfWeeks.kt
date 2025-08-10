package software.seriouschoi.timeisgold.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import software.seriouschoi.timeisgold.data.database.schema.TimeScheduleDayOfWeekSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeScheduleSchema

internal data class TimeScheduleWithDayOfWeeks(
    @Embedded val timeSchedule: TimeScheduleSchema,
    @Relation(
        parentColumn = "id",
        entityColumn = "timeScheduleId",
    )
    val dayOfWeeks: List<TimeScheduleDayOfWeekSchema>
)