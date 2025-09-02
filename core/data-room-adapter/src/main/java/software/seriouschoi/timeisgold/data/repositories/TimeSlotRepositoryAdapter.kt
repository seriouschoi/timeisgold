package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotSchema
import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

internal class TimeSlotRepositoryAdapter @Inject constructor(
    private val database: AppDatabase,
) : TimeSlotRepositoryPort {
    override suspend fun addTimeSlot(timeSlotData: TimeSlotComposition, timeRoutineUuid: String) {
        database.withTransaction {
            val timeRoutine = database.TimeRoutineDao().observe(timeRoutineUuid).first()
            val timeRoutineId =
                timeRoutine?.id ?: throw IllegalStateException("time routine is null")

            timeSlotData.timeSlotData.toTimeSlotSchema(timeRoutineId).let {
                database.TimeSlotDao().insert(it)
            }.takeIf { it > 0 } ?: throw IllegalStateException("time slot insert failed.")
        }
    }

    override suspend fun getTimeSlotDetail(timeslotUuid: String): Flow<TimeSlotComposition?> {
        return database.TimeSlotDao()
            .get(timeslotUuid)
            .distinctUntilChanged()
            .map {
                it?.toTimeSlotEntity()?.let {
                    TimeSlotComposition(
                        timeSlotData = it
                    )
                }
            }
    }

    override suspend fun getTimeSlotList(timeRoutineUuid: String): Flow<List<TimeSlotEntity>> {
        val dao = database.TimeRoutineJoinTimeSlotViewDao()
        return dao.observeTimeSlotsByTimeRoutine(timeRoutineUuid).distinctUntilChanged().map {
            it.map { it.toTimeSlotEntity() }
        }
    }

    override suspend fun setTimeSlot(timeSlotData: TimeSlotComposition) {
        database.withTransaction {
            updateTimeSlot(timeSlotData.timeSlotData)
        }
    }

    override suspend fun deleteTimeSlot(timeslotUuid: String) {
        database.withTransaction {
            database.TimeSlotDao().delete(timeslotUuid)
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


