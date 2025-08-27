package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotSchema
import software.seriouschoi.timeisgold.domain.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

internal class TimeSlotRepositoryAdapter @Inject constructor(
    private val database: AppDatabase,
) : TimeSlotRepositoryPort {
    override suspend fun addTimeSlot(timeSlotData: TimeSlotComposition, timeRoutineUuid: String) {
        database.withTransaction {
            val timeRoutine = database.TimeRoutineDao().get(timeRoutineUuid).first()
            val timeRoutineId =
                timeRoutine?.id ?: throw IllegalStateException("time routine is null")

            timeSlotData.timeSlotData.toTimeSlotSchema(timeRoutineId).let {
                database.TimeSlotDao().insert(it)
            }.takeIf { it > 0 } ?: throw IllegalStateException("time slot insert failed.")
        }
    }

    override suspend fun getTimeSlotDetail(timeslotUuid: String): TimeSlotComposition? {
        val timeSlotDto = database.TimeSlotDao().get(timeslotUuid).first() ?: return null

        val timeSlotEntity = timeSlotDto.toTimeSlotEntity()
        return TimeSlotComposition(
            timeSlotData = timeSlotEntity
        )
    }

    override suspend fun getTimeSlotList(timeRoutineUuid: String): List<TimeSlotEntity> {
        val list = database.TimeRoutineJoinTimeSlotViewDao().getTimeSlotsByTimeRoutine(timeRoutineUuid).first()
        return list.map { it.toTimeSlotEntity() }
    }

    override suspend fun setTimeSlot(timeSlotData: TimeSlotComposition) {
        database.withTransaction {
            updateTimeSlot(timeSlotData.timeSlotData)
        }
    }

    override suspend fun deleteTimeSlot(timeslotUuid: String) {
        database.withTransaction {
            val timeSlot = database.TimeSlotDao().get(timeslotUuid).first() ?: return@withTransaction

            //delete entities
            database.TimeSlotDao().delete(timeSlot)
        }
    }

    private suspend fun updateTimeSlot(timeSlotData: TimeSlotEntity) {
        database.withTransaction {
            val timeSlotEntity = database.TimeSlotDao().get(timeSlotData.uuid).first()
                ?: throw IllegalStateException("time slot is null")

            timeSlotData.toTimeSlotSchema(
                timeRoutineId = timeSlotEntity.timeRoutineId,
                timeSlotId = timeSlotEntity.id
            ).let {
                database.TimeSlotDao().update(it)
            }
        }
    }
}


