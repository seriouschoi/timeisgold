package software.seriouschoi.timeisgold.domain.usecase.time_scheduler

import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository

class DeleteTimeScheduleUseCase(
    private val timeScheduleRepository: TimeScheduleRepository
) {
    suspend operator fun invoke(uuid: String) {
        timeScheduleRepository.deleteTimeSchedule(uuid)
    }
}