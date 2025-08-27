package software.seriouschoi.timeisgold.domain.data.timeroutine

import software.seriouschoi.timeisgold.domain.entities.TimeRoutineDayOfWeekEntity

@Deprecated("use TimeRoutineEntity")
data class TimeRoutineData(
    val title: String,
    val uuid: String,
    val createTime: Long,
    val dayOfWeekList: List<TimeRoutineDayOfWeekEntity>
)
