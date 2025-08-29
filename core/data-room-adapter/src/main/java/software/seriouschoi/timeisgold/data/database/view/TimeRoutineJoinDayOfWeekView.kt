package software.seriouschoi.timeisgold.data.database.view

import androidx.room.DatabaseView
import java.time.DayOfWeek

@DatabaseView("""
    SELECT
        r.id AS routineId,
        r.uuid AS routineUuid,
        r.title AS routineName,
        r.createTime AS routineCreateTime,
        
        d.id AS dayOfWeekId,
        d.dayOfWeek AS dayOfWeek
        
    FROM TimeRoutineSchema r
    INNER JOIN TimeRoutineDayOfWeekSchema d ON r.id = d.timeRoutineId
    ORDER BY routineCreateTime DESC, dayOfWeek ASC
""")
internal data class TimeRoutineJoinDayOfWeekView(
    val routineId: Long,
    val routineUuid: String,
    val routineName: String,
    val routineCreateTime: Long,

    val dayOfWeek: DayOfWeek,
    val dayOfWeekId: Long,
)