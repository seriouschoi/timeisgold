package software.seriouschoi.timeisgold.domain.port

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity

interface TimeSlotRepositoryPort {

    suspend fun watchTimeSlotDetail(timeslotUuid: String): Flow<TimeSlotEntity?>
    suspend fun watchTimeSlotList(timeRoutineUuid: String): Flow<List<TimeSlotEntity>>
    suspend fun getTimeSlotList(timeRoutineUuid: String): List<TimeSlotEntity>

    suspend fun deleteTimeSlot(timeslotUuid: String) : DataResult<Unit>
    suspend fun setTimeSlot(
        timeSlotData: TimeSlotEntity,
        timeRoutineUuid: String
    ): DataResult<String>
}
