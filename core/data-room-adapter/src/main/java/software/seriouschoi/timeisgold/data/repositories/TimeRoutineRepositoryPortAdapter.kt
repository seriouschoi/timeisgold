package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.core.common.util.runSuspendCatching
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineEntity
import software.seriouschoi.timeisgold.data.util.asDataResult
import software.seriouschoi.timeisgold.domain.data.DataError
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 29.
 * jhchoi
 */
internal class TimeRoutineRepositoryPortAdapter @Inject constructor(
    private val database: AppDatabase
) : TimeRoutineRepositoryPort {

    override suspend fun setTimeRoutine(
        timeRoutine: TimeRoutineVO,
        routineId: String?
    ): DataResult<MetaInfo> = runSuspendCatching {
        val routineDao = database.TimeRoutineDao()
        val dayOfWeeksDao = database.TimeRoutineDayOfWeekDao()

        database.withTransaction {
            val routineEntity = routineId?.let {
                routineDao.get(it)
            }

            //update routine.
            val upsertedMetaInfo = upsert(
                timeRoutine = timeRoutine,
                routineUuid = routineEntity?.uuid
            )

            val routineDbId = routineDao.get(upsertedMetaInfo.uuid)?.id
                ?: throw IllegalStateException("routine is null")

            //update dayOfWeeks
            dayOfWeeksDao.delete(
                timeRoutineId = routineDbId,
            )

            timeRoutine.dayOfWeeks.forEach {
                dayOfWeeksDao.add(
                    TimeRoutineDayOfWeekEntity(
                        timeRoutineId = routineDbId,
                        dayOfWeek = it
                    )
                )
            }

            upsertedMetaInfo
        }

    }.asDataResult()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun watchRoutine(dayOfWeek: DayOfWeek): Flow<DataResult<MetaEnvelope<TimeRoutineVO>?>> {
        val routineDao = database.TimeRoutineDao()
        val dayOfWeekDao = database.TimeRoutineDayOfWeekDao()

        return dayOfWeekDao.watchLatest(
            dayOfWeek
        ).map { it?.timeRoutineId }.flatMapLatest { routineId ->
            if (routineId == null) {
                flowOf(DataResult.Success(null))
            } else {
                combine(
                    routineDao.watch(routineId),
                    dayOfWeekDao.watch(routineId)
                ) { routine, dayOfWeeks ->
                    if (routine == null) {
                        return@combine DataResult.Success(null)
                    }

                    val routineMeta = MetaInfo(
                        uuid = routine.uuid,
                        createTime = routine.createTime
                    )
                    val routineVO = TimeRoutineVO(
                        title = routine.title,
                        dayOfWeeks = dayOfWeeks.map { it.dayOfWeek }.toSet()
                    )
                    return@combine DataResult.Success(MetaEnvelope(routineVO, routineMeta))
                }
            }
        }
    }

    override fun watchAllDayOfWeeks(): Flow<DataResult<Set<DayOfWeek>>> {
        val dayOfWeekDao = database.TimeRoutineDayOfWeekDao()
        return dayOfWeekDao.watchAll().map {
            it.map {
                it.dayOfWeek
            }.toSet()
        }.map {
            DataResult.Success(it)
        }
    }

    override suspend fun deleteRoutine(routineId: String): DataResult<Unit> {
        val routineDao = database.TimeRoutineDao()
        val dayOfWeekDao = database.TimeRoutineDayOfWeekDao()

        return database.withTransaction {
            val routine = routineDao.get(routineId)
                ?: return@withTransaction DataResult.Failure(DataError.NotFound)

            routineDao.delete(routine)

            dayOfWeekDao.delete(routine.id)

            return@withTransaction DataResult.Success(Unit)
        }
    }

    /**
     * @return routineId
     */
    private suspend fun upsert(timeRoutine: TimeRoutineVO, routineUuid: String?): MetaInfo {
        val routineDao = database.TimeRoutineDao()
        val oldRoutine = routineUuid?.let { routineDao.get(it) }

        val metaInfo = if (oldRoutine == null) {
            MetaInfo.createNew()
        } else {
            MetaInfo(
                uuid = oldRoutine.uuid,
                createTime = oldRoutine.createTime
            )
        }

        val routineForAdd = TimeRoutineEntity(
            uuid = metaInfo.uuid,
            createTime = metaInfo.createTime,
            title = timeRoutine.title
        )

        if (oldRoutine == null) {
            routineDao.add(routineForAdd)
        } else {
            routineDao.update(routineForAdd.copy(id = oldRoutine.id))
        }

        return metaInfo
    }
}