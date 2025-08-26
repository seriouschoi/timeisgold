package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.mapper.toDomain
import software.seriouschoi.timeisgold.data.mapper.toSchema
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineData
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDayOfWeekData
import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDetailData
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

internal class TimeRoutineRepositoryAdapter @Inject constructor(
    private val appDatabase: AppDatabase
) : TimeRoutineRepositoryPort {
    override suspend fun addTimeRoutine(timeRoutine: TimeRoutineData) {
        appDatabase.withTransaction {
            appDatabase.TimeRoutineDao().add(
                timeRoutine.toSchema()
            )

            updateDayOfWeekList(
                routineDayOfWeekList = timeRoutine.dayOfWeekList,
                timeRoutineUuid = timeRoutine.uuid
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getTimeRoutineDetailFlow(week: DayOfWeek): Flow<TimeRoutineDetailData?> {
        val routineDao = appDatabase.TimeRoutineDao()
        val routineRelationDao = appDatabase.TimeRoutineRelationDao()
        return routineDao.getLatestUuidByDayOfWeekFlow(week)
            .distinctUntilChanged()
            .flatMapLatest { uuid ->
                if (uuid == null) {
                    flowOf(null)
                } else {
                    routineRelationDao.getFlow(uuid).map { it?.toDomain() }
                }
            }.distinctUntilChanged()
    }

    @Deprecated("Use getTimeRoutineDetailFlow")
    override suspend fun getTimeRoutineDetail(week: DayOfWeek): TimeRoutineDetailData? {
        return appDatabase.withTransaction {
            val timeRoutineUuid =
                appDatabase.TimeRoutineDao().getLatestUuidByDayOfWeekFlow(dayOfWeek = week).first() ?: return@withTransaction null

            appDatabase.TimeRoutineRelationDao().get(timeRoutineUuid)?.toDomain()
        }
    }


    override suspend fun setTimeRoutine(timeRoutine: TimeRoutineData) {
        appDatabase.withTransaction {
            val routineId = appDatabase.TimeRoutineDao().get(timeRoutine.uuid)?.id
                ?: throw IllegalStateException("time routine is null")
            appDatabase.TimeRoutineDao().update(
                timeRoutine.toSchema(id = routineId)
            )

            updateDayOfWeekList(timeRoutine.dayOfWeekList, timeRoutine.uuid)
        }
    }

    override suspend fun deleteTimeRoutine(timeRoutineUuid: String) {
        appDatabase.withTransaction {
            val timeRoutineEntity =
                appDatabase.TimeRoutineDao().get(timeRoutineUuid) ?: return@withTransaction

            // remove time routine.
            appDatabase.TimeRoutineDao().delete(timeRoutineEntity)
        }
    }


    override suspend fun getTimeRoutineDetailByUuid(timeRoutineUuid: String): TimeRoutineDetailData? {
        return appDatabase.withTransaction {
            appDatabase.TimeRoutineRelationDao().get(timeRoutineUuid)?.toDomain()
        }
    }

    private suspend fun updateDayOfWeekList(
        routineDayOfWeekList: List<TimeRoutineDayOfWeekData>,
        timeRoutineUuid: String
    ) {
        appDatabase.withTransaction {
            val timeRoutineRelation =
                appDatabase.TimeRoutineRelationDao().get(timeRoutineUuid)
                    ?: throw IllegalStateException("time routine relation is null")

            val timeRoutineId = timeRoutineRelation.timeRoutine.id
                ?: throw IllegalStateException("time routine id is null")

            appDatabase.TimeRoutineDayOfWeekDao().delete(timeRoutineId)
            //새로운 요일들.
            routineDayOfWeekList.forEach {
                appDatabase.TimeRoutineDayOfWeekDao().add(
                    it.toSchema(timeRoutineId)
                )
            }
        }
    }

    override suspend fun getAllTimeRoutines(): List<TimeRoutineData> {
        return appDatabase.withTransaction {
            appDatabase.TimeRoutineWithDayOfWeeksDao().getAll().map {
                it.toDomain()
            }
        }
    }
}


