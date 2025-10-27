package software.seriouschoi.timeisgold.domain.port

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import java.time.DayOfWeek

interface TimeSlotRepositoryPort {

    suspend fun watchTimeSlotDetail(timeslotUuid: String): Flow<TimeSlotEntity?>
    suspend fun watchTimeSlotList(timeRoutineUuid: String): Flow<List<TimeSlotEntity>>
    suspend fun getTimeSlotList(timeRoutineUuid: String): List<TimeSlotEntity>

    suspend fun deleteTimeSlot(timeslotUuid: String): DataResult<Unit>
    suspend fun setTimeSlot(
        timeSlotData: TimeSlotEntity,
        timeRoutineUuid: String
    ): DataResult<String>

    suspend fun setTimeSlot(
        timeSlotEnvelope: MetaEnvelope<TimeSlotVO>,
        dayOfWeek: DayOfWeek
    ): DataResult<MetaInfo>

}
