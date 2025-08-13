package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotMemoSchema
import software.seriouschoi.timeisgold.data.mapper.toDomain
import software.seriouschoi.timeisgold.data.mapper.toSchema
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotMemoData
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository
import javax.inject.Inject

internal class TimeSlotRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
) : TimeSlotRepository {
    override suspend fun addTimeSlot(timeSlotData: TimeSlotDetailData, timeRoutineUuid: String) {
        database.withTransaction {
            val timeRoutine = database.TimeRoutineDao().get(timeRoutineUuid)
            val timeRoutineId =
                timeRoutine?.id ?: throw IllegalStateException("time routine is null")

            val timeSlotId = timeSlotData.timeSlotData.let {
                it.toSchema(timeRoutineId)
            }.let {
                database.TimeSlotDao().insert(it)
            }.takeIf { it > 0 } ?: throw IllegalStateException("time slot insert failed.")

            if (timeSlotData.timeSlotMemoData != null) {
                timeSlotData.timeSlotMemoData?.toSchema(timeSlotId = timeSlotId)?.let {
                    database.TimeSlotMemoDao().insert(it)
                }?.takeIf { it > 0 } ?: throw IllegalStateException("time slot insert failed.")
            }
        }
    }

    override suspend fun getTimeSlotDetail(timeslotUuid: String): TimeSlotDetailData? {
        return database.TimeSlotRelationDao().getRelation(timeSlotUuid = timeslotUuid)?.toDomain()
    }

    override suspend fun setTimeSlot(timeSlotData: TimeSlotDetailData) {
        database.withTransaction {
            updateTimeSlot(timeSlotData.timeSlotData)

            val timeSlotRelation =
                database.TimeSlotRelationDao().getRelation(timeSlotData.timeSlotData.uuid)
                    ?: throw IllegalStateException("time slot relation is null")

            val timeSlotId =
                timeSlotRelation.timeSlot.id ?: throw IllegalStateException("time slot id is null")

            val memoData = timeSlotData.timeSlotMemoData
            if (memoData != null) {
                updateMemo(memoData, timeSlotId)
            } else {
                //delete memo.
                timeSlotRelation.memo?.let {
                    database.TimeSlotMemoDao().delete(it)
                }
            }
        }
    }

    override suspend fun deleteTimeSlot(timeslotUuid: String) {
        database.withTransaction {
            val timeSlot = database.TimeSlotDao().get(timeslotUuid) ?: return@withTransaction

            //delete entities
            database.TimeSlotDao().delete(timeSlot)
        }
    }

    private suspend fun updateMemo(memoData: TimeSlotMemoData, timeslotId: Long) {
        database.withTransaction {
            val entity = database.TimeSlotMemoDao().get(memoData.uuid)
            val newEntity = memoData.let {
                TimeSlotMemoSchema(
                    memo = it.memo,
                    uuid = it.uuid,
                    createTime = it.createTime,
                    timeSlotId = timeslotId,
                    id = entity?.id
                )
            }
            if (newEntity.id != null) {
                //update.
                database.TimeSlotMemoDao().update(newEntity)
            } else {
                //insert
                database.TimeSlotMemoDao().insert(newEntity)
            }
        }
    }

    private suspend fun updateTimeSlot(timeSlotData: TimeSlotData) {
        database.withTransaction {
            val timeSlotEntity = database.TimeSlotDao().get(timeSlotData.uuid)
                ?: throw IllegalStateException("time slot is null")

            timeSlotData.toSchema(
                timeRoutineId = timeSlotEntity.timeRoutineId,
                timeSlotId = timeSlotEntity.id
            ).let {
                database.TimeSlotDao().update(it)
            }
        }
    }
}