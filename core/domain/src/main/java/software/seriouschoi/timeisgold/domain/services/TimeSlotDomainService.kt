package software.seriouschoi.timeisgold.domain.services

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.exception.TIGException
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

class TimeSlotDomainService @Inject constructor(
    val timeSlotRepository: TimeSlotRepositoryPort,
) {

    suspend fun checkCanAdd(
        routineUuid: String,
        timeSlotDataForAdd: TimeSlotEntity,
    ) {
        val allTimeSlotList = timeSlotRepository.getTimeSlotList(routineUuid).first()
        //timeslot의 시간이 겹치는지 확인하는 로직.
        val isDuplicateTime = allTimeSlotList.any {
            timeSlotDataForAdd.startTime in (it.startTime..it.endTime)
                    || timeSlotDataForAdd.endTime in (it.startTime..it.endTime)
        }
        if (isDuplicateTime) {
            // TODO: jhchoi 2025. 9. 2. domain의 오류는 예측 가능한 오류이므로, throw하지 않는다.
            throw TIGException.TimeSlotConflict(timeSlotDataForAdd)
        }
    }
}