package software.seriouschoi.timeisgold.domain.usecase.time_scheduler

import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository

class AddTimeScheduleUseCase(
    private val timeScheduleRepository: TimeScheduleRepository
) {
    suspend operator fun invoke(timeSchedule: TimeScheduleData) {
        timeScheduleRepository.addTimeSchedule(timeSchedule)
    }
}