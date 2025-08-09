package software.seriouschoi.timeisgold.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import software.seriouschoi.timeisgold.data.database.entities.TimeScheduleDayOfWeekEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeScheduleEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotEntity

internal data class TimeScheduleRelation(
    @Embedded val timeSchedule: TimeScheduleEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "timeScheduleId",
    )
    val timeSlots: List<TimeSlotEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "timeScheduleId",
    )
    val dayOfWeeks: List<TimeScheduleDayOfWeekEntity>
)