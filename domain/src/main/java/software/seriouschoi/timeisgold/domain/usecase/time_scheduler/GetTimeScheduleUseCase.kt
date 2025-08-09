package software.seriouschoi.timeisgold.domain.usecase.time_scheduler

import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleDetailData
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository
import java.time.DayOfWeek

class GetTimeScheduleUseCase (
    private val timeScheduleRepository: TimeScheduleRepository
) {
    suspend operator fun invoke(week: DayOfWeek) : TimeScheduleDetailData? {
        return timeScheduleRepository.getTimeScheduleDetail(week)
    }
}