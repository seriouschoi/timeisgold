package software.seriouschoi.timeisgold.domain.data.time_schedule

data class TimeScheduleData(
    val timeScheduleName: String,
    val uuid: String,
    val createTime: Long,
    val dayOfWeekList: List<TimeScheduleDayOfWeekData>
)
