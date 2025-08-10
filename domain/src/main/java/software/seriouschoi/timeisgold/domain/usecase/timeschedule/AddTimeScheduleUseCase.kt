package software.seriouschoi.timeisgold.domain.usecase.timeschedule

import software.seriouschoi.timeisgold.domain.data.timeschedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.policy.TimeSchedulePolicy
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository

class AddTimeScheduleUseCase(
    private val timeScheduleRepository: TimeScheduleRepository,
    private val timeSchedulePolicy: TimeSchedulePolicy
) {
    suspend operator fun invoke(timeSchedule: TimeScheduleData) {
        val allTimeScheduleList = timeScheduleRepository.getAllTimeSchedules()
        timeSchedulePolicy.checkCanAdd(
            allTimeScheduleList, timeSchedule
        )
        timeScheduleRepository.addTimeSchedule(timeSchedule)
    }
}