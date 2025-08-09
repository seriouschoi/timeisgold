package software.seriouschoi.timeisgold.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import software.seriouschoi.timeisgold.data.database.entities.TimeScheduleDayOfWeekEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeScheduleEntity

internal data class TimeScheduleWithDayOfWeeks(
    @Embedded val timeSchedule: TimeScheduleEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "timeScheduleId",
    )
    val dayOfWeeks: List<TimeScheduleDayOfWeekEntity>
)