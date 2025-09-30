package software.seriouschoi.timeisgold.domain.services

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.core.common.util.asMinutes
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject
import kotlin.math.abs

class TimeSlotDomainService @Inject constructor(
    val timeSlotRepository: TimeSlotRepositoryPort,
) {

    suspend fun isValid(
        routineUuid: String,
        timeSlotData: TimeSlotEntity,
    ): DomainResult<Unit> {
        val newSlotTitle = timeSlotData.title
        if (newSlotTitle.length !in 1..15) {
            return DomainResult.Failure(DomainError.Validation.TitleLength)
        }

        val allTimeSlotList = timeSlotRepository.watchTimeSlotList(routineUuid).first()

        if (abs(timeSlotData.endTime.asMinutes() - timeSlotData.startTime.asMinutes()) <= 15) {
            return DomainResult.Failure(DomainError.Conflict.Time)
        }

        //timeslot의 시간이 겹치는지 확인하는 로직.
        val isDuplicateTime = allTimeSlotList.filter {
            it.uuid != timeSlotData.uuid
        }.any {
            val existSlotRange = it.startTime.asMinutes() until it.endTime.asMinutes()
            val newSlotRange = timeSlotData.startTime.asMinutes() until timeSlotData.endTime.asMinutes()
            newSlotRange.any {
                existSlotRange.contains(it)
            }
        }
        if (isDuplicateTime) {
            return DomainResult.Failure(DomainError.Conflict.Data)
        }
        return DomainResult.Success(Unit)
    }
}