package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.core.common.util.runSuspendCatching
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotSchema
import software.seriouschoi.timeisgold.data.util.asDataResult
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort
import java.time.DayOfWeek
import java.util.UUID
import javax.inject.Inject

internal class TimeSlotRepositoryPortAdapter @Inject constructor(
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
        return dao.watchTimeSlotsByTimeRoutine(timeRoutineUuid).distinctUntilChanged().map {
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

    override suspend fun setTimeSlot(
        timeSlotEnvelope: MetaEnvelope<TimeSlotVO>,
        dayOfWeek: DayOfWeek
    ): DataResult<MetaInfo> = runSuspendCatching {
        val routineDayOfWeekDao = database.TimeRoutineJoinDayOfWeekViewDao()
        val timeSlotDao = database.TimeSlotDao()
        val dayOfWeekDao = database.TimeRoutineDayOfWeekDao()
        database.withTransaction {
            var routine = routineDayOfWeekDao.getLatestByDayOfWeek(dayOfWeek)
            if (routine == null) {
                val newRoutineEntity = TimeRoutineEntity(
                    uuid = UUID.randomUUID().toString(),
                    title = "",
                    createTime = 0
                )
                val routineId = database.TimeRoutineDao().upsert(newRoutineEntity)
                dayOfWeekDao.add(
                    dayOfWeek.toTimeRoutineDayOfWeekEntity()
                        .toTimeRoutineDayOfWeekSchema(
                            timeRoutineId = routineId
                        )
                )
                routine = routineDayOfWeekDao.getLatestByDayOfWeek(dayOfWeek)
            }
            if (routine == null) throw IllegalStateException("time routine is null")

            val timeSlot = timeSlotEnvelope.payload
            val slotMeta = timeSlotEnvelope.metaInfo ?: MetaInfo.createNew()
            val slotId = timeSlotDao.get(slotMeta.uuid)?.id

            val timeSlotSchema = TimeSlotSchema(
                id = slotId,
                startTime = timeSlot.startTime,
                endTime = timeSlot.endTime,
                title = timeSlot.title,
                uuid = slotMeta.uuid,
                createTime = slotMeta.createTime.toEpochSecond(),
                timeRoutineId = routine.routineId
            )
            timeSlotDao.upsert(
                timeSlotSchema
            )

            slotMeta
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


