package software.seriouschoi.timeisgold.domain.usecase.time_scheduler

import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.policy.TimeSchedulePolicy
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository

class SetTimeScheduleUseCase(
    private val timeScheduleRepository: TimeScheduleRepository,
    private val timeSchedulePolicy: TimeSchedulePolicy
) {
    suspend operator fun invoke(timeSchedule: TimeScheduleData) {
        val scheduleListForPolicy = timeScheduleRepository.getAllTimeSchedules()
        timeSchedulePolicy.checkCanAdd(
            scheduleListForPolicy, timeSchedule
        )
        timeScheduleRepository.setTimeSchedule(timeSchedule)
    }
}