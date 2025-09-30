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
        timeSlotData: TimeSlotEntity,
        routineUuid: String,
    ): DomainResult<Unit> {
        val newSlotTitle = timeSlotData.title
        if (newSlotTitle.length !in 1..15) {
            return DomainResult.Failure(DomainError.Validation.TitleLength)
        }

        val allTimeSlotList = timeSlotRepository.watchTimeSlotList(routineUuid).first()

        if (abs(timeSlotData.endTime.asMinutes() - timeSlotData.startTime.asMinutes()) <= 15) {
            return DomainResult.Failure(DomainError.Conflict.Time)
        }
        // TODO: jhchoi 2025. 9. 30. 자정 넘어가는 데이터 valid처리 누락됨.

        //timeslot의 시간이 겹치는지 확인하는 로직.
        val isDuplicateTime = allTimeSlotList.filter {
            it.uuid != timeSlotData.uuid
        }.any {
            val existSlotRange = it.startTime.asMinutes() until it.endTime.asMinutes()
            val newSlotRange =
                timeSlotData.startTime.asMinutes() until timeSlotData.endTime.asMinutes()
            newSlotRange.first in existSlotRange || newSlotRange.last in existSlotRange
        }
        if (isDuplicateTime) {
            return DomainResult.Failure(DomainError.Conflict.Data)
        }
        return DomainResult.Success(Unit)
    }

    suspend fun isValid(
        timeSlotList: List<TimeSlotEntity>
    ): DomainResult<Unit> {
        val sorted = timeSlotList.sortedBy {
            it.endTime.asMinutes()
        }.sortedBy {
            it.startTime.asMinutes()
        }

        var current: TimeSlotEntity? = null
        for (next in sorted) {
            if (current == null) {
                current = next
                continue
            }
            val currentRange = current.startTime.asMinutes() until current.endTime.asMinutes()
            val nextRange = next.startTime.asMinutes() until next.endTime.asMinutes()

            val overlap = currentRange.first in nextRange || currentRange.last in nextRange

            if (overlap) return DomainResult.Failure(DomainError.Conflict.Data)
        }

        return DomainResult.Success(Unit)
    }
}