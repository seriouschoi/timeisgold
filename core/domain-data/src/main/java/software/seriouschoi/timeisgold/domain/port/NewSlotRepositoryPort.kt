package software.seriouschoi.timeisgold.domain.port

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO

/**
 * Created by jhchoi on 2025. 10. 28.
 * jhchoi
 */
interface NewSlotRepositoryPort {
    suspend fun setTimeSlot(
        timeSlot: TimeSlotVO,
        slotId: String?,
        routineId: String
    ): DataResult<MetaInfo>

    suspend fun setTimeSlots(
        timeSlots: Map<String, TimeSlotVO>,
        routineId: String
    ): DataResult<List<MetaInfo>>

    fun watchTimeSlot(
        timeSlotId: String
    ): Flow<DataResult<MetaEnvelope<TimeSlotVO>>>

    fun watchTimeSlotList(
        routineId: String
    ): Flow<DataResult<List<MetaEnvelope<TimeSlotVO>>>>

    suspend fun deleteTimeSlot(
        timeSlotId: String
    ): DataResult<Unit>

}