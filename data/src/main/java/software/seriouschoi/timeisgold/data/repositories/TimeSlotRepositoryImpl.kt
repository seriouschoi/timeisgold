package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotMemoEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeSlot_TimeSlotMemoInfo_Entity
import software.seriouschoi.timeisgold.domain.data.TimeSlotData
import software.seriouschoi.timeisgold.domain.data.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.data.TimeSlotMemoData
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository
import javax.inject.Inject

internal class TimeSlotRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
) : TimeSlotRepository {
    override suspend fun addTimeSlot(timeSlotData: TimeSlotDetailData) {
        database.withTransaction {
            val timeSlotId = timeSlotData.timeSlotData.let {
                TimeSlotEntity(
                    uuid = it.uuid,
                    title = it.title,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    createTime = it.createTime
                )
            }.let {
                database.TimeSlotDao().insert(it)
            }.takeIf { it > 0 } ?: throw IllegalStateException("time slot id is null")

            val timeSlotMemoId = timeSlotData.timeSlotMemoData?.let {
                TimeSlotMemoEntity(
                    memo = it.memo,
                    uuid = it.uuid,
                    createTime = it.createTime
                )
            }?.let {
                database.TimeSlotMemoDao().insert(it)
            }?.takeIf { it > 0 }

            if (timeSlotMemoId != null) {
                TimeSlot_TimeSlotMemoInfo_Entity(
                    timeslotId = timeSlotId,
                    timeslotMemoId = timeSlotMemoId
                ).let {
                    database.TimeSlot_TimeSlotMemo_Dao().insert(it)
                }
            }
        }
    }

    override suspend fun setTimeSlot(timeSlotData: TimeSlotDetailData) {
        database.withTransaction {
            val timeslotId = database.TimeSlotDao().getId(timeSlotData.timeSlotData.uuid)
            requireNotNull(timeslotId) {
                "time slot id is null"
            }
            timeSlotData.timeSlotData.let {
                TimeSlotEntity(
                    id = timeslotId,
                    uuid = it.uuid,
                    title = it.title,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    createTime = it.createTime
                )
            }.let {
                database.TimeSlotDao().update(it)
            }


            timeSlotData.timeSlotMemoData?.let { memoData ->
                val memoId = database.TimeSlotMemoDao().getId(memoData.uuid)
                val entity = TimeSlotMemoEntity(
                    memo = memoData.memo,
                    uuid = memoData.uuid,
                    createTime = memoData.createTime,
                )
                if (memoId != null) {
                    entity.copy(id = memoId).let {
                        database.TimeSlotMemoDao().update(it)
                    }
                } else {
                    database.TimeSlotMemoDao().insert(entity)
                }
            }
        }
    }

    override suspend fun getTimeSlotList(): List<TimeSlotData> {
        return database.TimeSlotDao().getAll().map {
            TimeSlotData(
                uuid = it.uuid,
                title = it.title,
                startTime = it.startTime,
                endTime = it.endTime,
                createTime = it.createTime
            )
        }
    }

    override suspend fun getTimeSlot(timeslotUuid: String): TimeSlotDetailData? {
        return database.TimeSlotWithExtrasRelationDao().getRelation(timeSlotUuid = timeslotUuid)?.let {
            val timeSlot = TimeSlotData(
                uuid = it.timeSlot.uuid,
                title = it.timeSlot.title,
                startTime = it.timeSlot.startTime,
                endTime = it.timeSlot.endTime,
                createTime = it.timeSlot.createTime
            )
            val timeSlotMemo = it.memo?.let {
                TimeSlotMemoData(
                    uuid = it.uuid,
                    memo = it.memo,
                    createTime = it.createTime
                )
            }
            TimeSlotDetailData(
                timeSlotData = timeSlot,
                timeSlotMemoData = timeSlotMemo
            )
        }
    }

    override suspend fun deleteTimeSlot(uuid: String) {
        database.withTransaction {
            val relation = database.TimeSlotWithExtrasRelationDao().getRelation(timeSlotUuid = uuid)
                ?: return@withTransaction

            //delete entities
            database.TimeSlotDao().delete(relation.timeSlot)
            relation.memo?.let { database.TimeSlotMemoDao().delete(it) }

            val timeSlotId =
                relation.timeSlot.id ?: throw IllegalStateException("time slot id is null")
            val memoId = relation.memo?.id

            //delete relation.
            if (memoId != null) {
                database.TimeSlot_TimeSlotMemo_Dao().get(
                    timeslotId = timeSlotId,
                    timeslotMemoId = memoId
                )?.let {
                    database.TimeSlot_TimeSlotMemo_Dao().delete(it)
                }
            }
        }
    }
}