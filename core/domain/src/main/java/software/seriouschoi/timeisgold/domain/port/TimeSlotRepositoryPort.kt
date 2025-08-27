package software.seriouschoi.timeisgold.domain.port

import software.seriouschoi.timeisgold.domain.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.entities.TimeSlotEntity

interface TimeSlotRepositoryPort {
    suspend fun addTimeSlot(timeSlotData: TimeSlotComposition, timeRoutineUuid: String)
    suspend fun getTimeSlotDetail(timeslotUuid: String): TimeSlotComposition?
    suspend fun getTimeSlotList(timeRoutineUuid: String): List<TimeSlotEntity>
    suspend fun setTimeSlot(timeSlotData: TimeSlotComposition)
    suspend fun deleteTimeSlot(timeslotUuid: String)
}
