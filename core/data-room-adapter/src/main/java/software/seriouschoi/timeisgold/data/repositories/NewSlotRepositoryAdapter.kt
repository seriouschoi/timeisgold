package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.core.common.util.runSuspendCatching
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema
import software.seriouschoi.timeisgold.data.util.asDataResult
import software.seriouschoi.timeisgold.domain.data.DataError
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.port.NewSlotRepositoryPort
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 30.
 * jhchoi
 */
internal class NewSlotRepositoryAdapter @Inject constructor(
    private val database: AppDatabase
) : NewSlotRepositoryPort {
    override suspend fun setTimeSlot(
        timeSlot: TimeSlotVO,
        slotId: String?,
        routineId: String
    ): DataResult<MetaInfo> = runSuspendCatching {
        database.withTransaction {
            upsert(
                timeSlot = timeSlot,
                routineUuId = routineId,
                slotUuid = slotId
            )
        }
    }.asDataResult()

    override suspend fun setTimeSlots(
        timeSlots: Map<String, TimeSlotVO>,
        routineId: String
    ): DataResult<List<MetaInfo>> = runSuspendCatching {
        database.withTransaction {
            val routine = database.TimeRoutineDao().get(routineId)
                ?: throw IllegalStateException("routine is null")

            timeSlots.map {
                upsert(
                    timeSlot = it.value,
                    routineUuId = routine.uuid,
                    slotUuid = it.key
                )
            }
        }
    }.asDataResult()

    override fun watchTimeSlot(timeSlotId: String): Flow<DataResult<MetaEnvelope<TimeSlotVO>>> {
        val slotDao = database.TimeSlotDao()
        return slotDao.watch(timeSlotId).map { slot: TimeSlotSchema? ->
            if (slot == null) {
                return@map DataResult.Failure(DataError.NotFound)
            }

            val metaInfo = MetaInfo(
                uuid = slot.uuid,
                createTime = slot.createTime
            )
            val slotVo = TimeSlotVO(
                startTime = slot.startTime,
                endTime = slot.endTime,
                title = slot.title
            )
            return@map DataResult.Success(MetaEnvelope(slotVo, metaInfo))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun watchTimeSlotList(routineId: String): Flow<DataResult<List<MetaEnvelope<TimeSlotVO>>>> {
        val slotDao = database.TimeSlotDao()
        val routineDao = database.TimeRoutineDao()

        return routineDao.watch(routineId).flatMapLatest { routine: TimeRoutineSchema? ->
            if (routine == null) {
                flowOf(emptyList())
            } else {
                slotDao.watchList(
                    routineId = routine.id
                ).map { slotList: List<TimeSlotSchema> ->
                    slotList.map { slot ->
                        val metaInfo = MetaInfo(
                            uuid = slot.uuid,
                            createTime = slot.createTime
                        )
                        val slotVo = TimeSlotVO(
                            startTime = slot.startTime,
                            endTime = slot.endTime,
                            title = slot.title
                        )
                        MetaEnvelope(payload = slotVo, metaInfo = metaInfo)
                    }
                }
            }
        }.map {
            DataResult.Success(it)
        }
    }

    override suspend fun deleteTimeSlot(timeSlotId: String): DataResult<Unit> = runSuspendCatching {
        val slotDao = database.TimeSlotDao()
        slotDao.delete(timeSlotId)
    }.asDataResult()

    suspend fun upsert(
        timeSlot: TimeSlotVO,
        routineUuId: String,
        slotUuid: String?
    ): MetaInfo {
        val routineDao = database.TimeRoutineDao()
        val slotDao = database.TimeSlotDao()

        val routine = routineDao.get(routineUuId) ?: throw IllegalStateException("routine is null")

        val oldSlot = slotUuid?.let { slotDao.get(it) }
        val metaInfo = if (oldSlot == null) {
            MetaInfo.createNew()
        } else {
            MetaInfo(
                uuid = oldSlot.uuid,
                createTime = oldSlot.createTime
            )
        }

        val slotForAdd = TimeSlotSchema(
            startTime = timeSlot.startTime,
            endTime = timeSlot.endTime,
            title = timeSlot.title,
            uuid = metaInfo.uuid,
            createTime = metaInfo.createTime,
            timeRoutineId = routine.id
        )

        if (oldSlot == null) {
            slotDao.insert(slotForAdd)
        } else {
            slotDao.update(slotForAdd.copy(id = oldSlot.id))
        }

        return metaInfo
    }
}