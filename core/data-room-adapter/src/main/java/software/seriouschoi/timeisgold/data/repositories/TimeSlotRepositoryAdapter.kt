package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.core.common.util.runSuspendCatching
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotSchema
import software.seriouschoi.timeisgold.data.util.asDataResult
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeSlotComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import javax.inject.Inject

internal class TimeSlotRepositoryAdapter @Inject constructor(
    private val database: AppDatabase,
) : TimeSlotRepositoryPort {

    override suspend fun watchTimeSlotDetail(timeslotUuid: String): Flow<TimeSlotEntity?> {
        return database.TimeSlotDao()
            .observe(timeslotUuid)
            .distinctUntilChanged()
            .map {
                it?.toTimeSlotEntity()
            }
    }

    override suspend fun watchTimeSlotList(timeRoutineUuid: String): Flow<List<TimeSlotEntity>> {
        val dao = database.TimeRoutineJoinTimeSlotViewDao()
        return dao.observeTimeSlotsByTimeRoutine(timeRoutineUuid).distinctUntilChanged().map {
            it.map { it.toTimeSlotEntity() }
        }
    }

    override suspend fun getTimeSlotList(timeRoutineUuid: String): List<TimeSlotEntity> {
        val dao = database.TimeRoutineJoinTimeSlotViewDao()
        return dao.getTimeSlotsByTimeRoutine(timeRoutineUuid).map { it.toTimeSlotEntity() }
    }

    override suspend fun setTimeSlot(
        timeSlotData: TimeSlotEntity,
        timeRoutineUuid: String
    ): DataResult<String> = runSuspendCatching {
        val routineDao = database.TimeRoutineDao()
        val slotDao = database.TimeSlotDao()
        database.withTransaction {
            val routineId = routineDao.get(timeRoutineUuid)?.id
                ?: throw IllegalStateException("time routine is null")

            val slotId = slotDao.get(timeSlotData.uuid)?.id

            val slotSchema = timeSlotData.toTimeSlotSchema(
                timeRoutineId = routineId,
                timeSlotId = slotId
            )
            database.TimeSlotDao().upsert(slotSchema)

            slotSchema.uuid
        }
    }.asDataResult()

    override suspend fun deleteTimeSlot(
        timeslotUuid: String
    ): DataResult<Unit> = runSuspendCatching {
        database.withTransaction {
            database.TimeSlotDao().delete(timeslotUuid)
        }
    }.asDataResult()
}


