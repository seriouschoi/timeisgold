package software.seriouschoi.timeisgold.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import software.seriouschoi.timeisgold.data.database.entities.TimeScheduleDayOfWeekEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeScheduleEntity

/**
 * Created by jhchoi on 2025. 8. 7.
 * jhchoi@neofect.com
 */
internal data class TimeScheduleDayOfWeekRelation (
    @Embedded val dayOfWeekEntity: TimeScheduleDayOfWeekEntity,
    @Relation(
        parentColumn = "timeScheduleId",
        entityColumn = "id"
    )
    val timeSchedule: TimeScheduleEntity
)