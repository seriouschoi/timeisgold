package software.seriouschoi.timeisgold.domain.data.timeschedule

data class TimeScheduleData(
    val timeScheduleName: String,
    val uuid: String,
    val createTime: Long,
    val dayOfWeekList: List<TimeScheduleDayOfWeekData>
)
