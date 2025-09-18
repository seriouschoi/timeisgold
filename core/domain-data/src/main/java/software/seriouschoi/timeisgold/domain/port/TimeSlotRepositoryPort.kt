package software.seriouschoi.timeisgold.domain.port

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity

interface TimeSlotRepositoryPort {
    suspend fun addTimeSlot(timeSlotData: TimeSlotComposition, timeRoutineUuid: String)
    suspend fun watchTimeSlotDetail(timeslotUuid: String): Flow<TimeSlotComposition?>
    suspend fun observeTimeSlotList(timeRoutineUuid: String): Flow<List<TimeSlotEntity>>
    suspend fun setTimeSlot(timeSlotData: TimeSlotComposition)
    suspend fun deleteTimeSlot(timeslotUuid: String)
    suspend fun getTimeSlotList(timeRoutineUuid: String): List<TimeSlotEntity>
}
