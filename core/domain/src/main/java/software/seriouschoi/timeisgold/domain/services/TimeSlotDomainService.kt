package software.seriouschoi.timeisgold.domain.services

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

class TimeSlotDomainService @Inject constructor(
    val timeSlotRepository: TimeSlotRepositoryPort,
) {

    suspend fun isValid(
        routineUuid: String,
        timeSlotData: TimeSlotEntity,
    ) : DomainResult<Unit> {
        val allTimeSlotList = timeSlotRepository.watchTimeSlotList(routineUuid).first()
        //timeslot의 시간이 겹치는지 확인하는 로직.
        val isDuplicateTime = allTimeSlotList.any {
            timeSlotData.startTime in (it.startTime..it.endTime)
                    || timeSlotData.endTime in (it.startTime..it.endTime)
        }
        if(isDuplicateTime) {
            return DomainResult.Failure(DomainError.Conflict.Data)
        }
        return DomainResult.Success(Unit)
    }
}