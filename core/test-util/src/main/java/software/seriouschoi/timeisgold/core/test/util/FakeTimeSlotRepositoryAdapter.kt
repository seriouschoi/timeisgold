package software.seriouschoi.timeisgold.core.test.util

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort

/**
 * Created by jhchoi on 2025. 9. 5.
 * jhchoi
 */
class FakeTimeSlotRepositoryAdapter(
    private val mockTimeRoutines: List<TimeRoutineComposition>
) : TimeSlotRepositoryPort {
    override suspend fun watchTimeSlotDetail(timeslotUuid: String): Flow<TimeSlotEntity?> {
        TODO("Not yet implemented")
    }

    override suspend fun watchTimeSlotList(timeRoutineUuid: String): Flow<List<TimeSlotEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTimeSlotList(timeRoutineUuid: String): List<TimeSlotEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTimeSlot(timeslotUuid: String): DataResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun setTimeSlot(
        timeSlotData: TimeSlotEntity,
        timeRoutineUuid: String
    ): DataResult<String> {
        TODO("Not yet implemented")
    }
}