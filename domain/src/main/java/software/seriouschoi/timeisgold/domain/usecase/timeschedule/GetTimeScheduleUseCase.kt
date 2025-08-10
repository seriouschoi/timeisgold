package software.seriouschoi.timeisgold.domain.usecase.timeschedule

import software.seriouschoi.timeisgold.domain.data.timeschedule.TimeScheduleDetailData
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository
import java.time.DayOfWeek

class GetTimeScheduleUseCase (
    private val timeScheduleRepository: TimeScheduleRepository
) {
    suspend operator fun invoke(week: DayOfWeek) : TimeScheduleDetailData? {
        return timeScheduleRepository.getTimeScheduleDetail(week)
    }
}