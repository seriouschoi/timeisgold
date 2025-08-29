package software.seriouschoi.timeisgold.data.database.view

import androidx.room.DatabaseView
import java.time.LocalTime

@DatabaseView("""
    SELECT
        r.id AS routineId,
        r.uuid AS routineUuid,
        r.title AS routineName,
        r.createTime AS routineCreateTime,
        
        ts.id AS timeSlotId,
        ts.startTime AS timeSlotStartTime,
        ts.endTime AS timeSlotEndTime,
        ts.title AS timeSlotTitle,
        ts.uuid AS timeSlotUuid,
        ts.createTime AS timeSlotCreateTime
    FROM TimeRoutineSchema r
    JOIN TimeSlotSchema ts ON r.id = ts.timeRoutineId
""")
data class TimeRoutineJoinTimeSlotView(
    val routineId: Long,
    val routineUuid: String,
    val routineName: String,
    val routineCreateTime: Long,

    val timeSlotId: Long,
    val timeSlotStartTime: LocalTime,
    val timeSlotEndTime: LocalTime,
    val timeSlotTitle: String,
    val timeSlotUuid: String,
    val timeSlotCreateTime: Long,
)
