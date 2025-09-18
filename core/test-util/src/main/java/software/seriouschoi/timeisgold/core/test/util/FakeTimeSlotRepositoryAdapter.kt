package software.seriouschoi.timeisgold.core.test.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort

/**
 * Created by jhchoi on 2025. 9. 5.
 * jhchoi
 */
class FakeTimeSlotRepositoryAdapter(
    private val mockTimeRoutines: List<TimeRoutineComposition>
) : TimeSlotRepositoryPort {
    override suspend fun addTimeSlot(
        timeSlotData: TimeSlotComposition,
        timeRoutineUuid: String
    ) {
    }

    override suspend fun watchTimeSlotDetail(timeslotUuid: String): Flow<TimeSlotComposition?> {
        val result = mockTimeRoutines.map {
            it.timeSlots.filter {
                it.uuid == timeslotUuid
            }
        }.flatten().first().let {
            TimeSlotComposition(it)
        }
        return flowOf(result)
    }

    override suspend fun observeTimeSlotList(timeRoutineUuid: String): Flow<List<TimeSlotEntity>> {
        val result= mockTimeRoutines.find {
            it.timeRoutine.uuid == timeRoutineUuid
        }?.timeSlots ?: emptyList()
        return flowOf(result)
    }

    override suspend fun setTimeSlot(timeSlotData: TimeSlotComposition) {
    }

    override suspend fun deleteTimeSlot(timeslotUuid: String) {
    }

    override suspend fun getTimeSlotList(timeRoutineUuid: String): List<TimeSlotEntity> {
        return  mockTimeRoutines.find {
            it.timeRoutine.uuid == timeRoutineUuid
        }?.timeSlots ?: emptyList()
    }
}