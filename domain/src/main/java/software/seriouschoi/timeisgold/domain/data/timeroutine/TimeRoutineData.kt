package software.seriouschoi.timeisgold.domain.data.timeroutine

data class TimeRoutineData(
    val title: String,
    val uuid: String,
    val createTime: Long,
    val dayOfWeekList: List<TimeRoutineDayOfWeekData>
)
