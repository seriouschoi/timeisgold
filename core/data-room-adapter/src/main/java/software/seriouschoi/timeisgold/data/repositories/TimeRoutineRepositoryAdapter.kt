package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.mapper.schemaToTimeRoutineEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineSchemaSchema
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotEntity
import software.seriouschoi.timeisgold.domain.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class TimeRoutineRepositoryAdapter @Inject constructor(
    private val appDatabase: AppDatabase
) : TimeRoutineRepositoryPort {
    override suspend fun addTimeRoutineComposition(timeRoutine: TimeRoutineComposition) {
        appDatabase.withTransaction {
            appDatabase.TimeRoutineDao().add(
                timeRoutine.timeRoutine.toTimeRoutineSchemaSchema(null)
            )

            updateDayOfWeekList(
                routineDayOfWeekList = timeRoutine.dayOfWeeks,
                timeRoutineUuid = timeRoutine.timeRoutine.uuid
            )
        }
    }

    override fun getTimeRoutineCompositionByDayOfWeek(dayOfWeek: DayOfWeek): Flow<TimeRoutineComposition?> {
        return appDatabase.TimeRoutineJoinDayOfWeekViewDao()
            .getLatestByDayOfWeek(dayOfWeek)
            .distinctUntilChanged()
            .map {
                it?.toTimeRoutineEntity()
            }
            .flatMapLatest { routine: TimeRoutineEntity? ->
                if (routine == null) flowOf(null)
                else {
                    combine(
                        observeWeeks(routine.uuid),
                        observeSlots(routine.uuid)
                    ) { weeks, slots ->
                        TimeRoutineComposition(
                            timeRoutine = routine,
                            dayOfWeeks = weeks,
                            timeSlots = slots
                        )
                    }
                }
            }
            .distinctUntilChanged()
    }

    private fun observeWeeks(routineUuid: String): Flow<List<TimeRoutineDayOfWeekEntity>> =
        appDatabase.TimeRoutineJoinDayOfWeekViewDao()
            .getDayOfWeeksByTimeRoutine(routineUuid)
            .map { dayOfWeeks: List<DayOfWeek> ->
                dayOfWeeks.map {
                    it.toTimeRoutineDayOfWeekEntity()
                }
            }
            .distinctUntilChanged()

    private fun observeSlots(routineUuid: String): Flow<List<TimeSlotEntity>> =
        appDatabase.TimeRoutineJoinTimeSlotViewDao()
            .getTimeSlotsByTimeRoutine(routineUuid)
            .map { list ->
                list.map {
                    it.toTimeSlotEntity()
                }
            }
            .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getTimeRoutineByDayOfWeek(day: DayOfWeek): Flow<TimeRoutineEntity?> {
        val dao = appDatabase.TimeRoutineJoinDayOfWeekViewDao()
        return dao.getLatestByDayOfWeek(day).distinctUntilChanged().map {
            it?.toTimeRoutineEntity()
        }.distinctUntilChanged()
    }

    override suspend fun setTimeRoutineComposition(composition: TimeRoutineComposition) {
        appDatabase.withTransaction {
            val routineId =
                appDatabase.TimeRoutineDao().get(composition.timeRoutine.uuid).first()?.id
                    ?: throw IllegalStateException("time routine is null")
            appDatabase.TimeRoutineDao().update(
                composition.timeRoutine.toTimeRoutineSchemaSchema(id = routineId)
            )

            updateDayOfWeekList(composition.dayOfWeeks, composition.timeRoutine.uuid)
        }
    }

    override suspend fun deleteTimeRoutine(timeRoutineUuid: String) {
        appDatabase.withTransaction {
            val timeRoutineEntity =
                appDatabase.TimeRoutineDao().get(timeRoutineUuid).first() ?: return@withTransaction

            // remove time routine.
            appDatabase.TimeRoutineDao().delete(timeRoutineEntity)
        }
    }


    override fun getAllDayOfWeeks(): Flow<List<DayOfWeek>> {
        return appDatabase.TimeRoutineJoinDayOfWeekViewDao().getAllDayOfWeeks()
    }


    override fun getTimeRoutineCompositionByUuid(timeRoutineUuid: String): Flow<TimeRoutineComposition?> {
        return appDatabase.TimeRoutineDao()
            .get(timeRoutineUuid)
            .distinctUntilChanged()
            .map {
                it?.schemaToTimeRoutineEntity()
            }
            .flatMapLatest {
                if (it == null) flowOf(null)
                else {
                    combine(
                        observeWeeks(it.uuid),
                        observeSlots(it.uuid)
                    ) { weeks, slots ->
                        TimeRoutineComposition(
                            timeRoutine = it,
                            dayOfWeeks = weeks,
                            timeSlots = slots
                        )
                    }
                }
            }
            .distinctUntilChanged()
    }

    private suspend fun updateDayOfWeekList(
        routineDayOfWeekList: List<TimeRoutineDayOfWeekEntity>,
        timeRoutineUuid: String
    ) {
        appDatabase.withTransaction {
            val timeRoutine =
                appDatabase.TimeRoutineDao().get(timeRoutineUuid).first() ?: return@withTransaction
            val timeRoutineId = timeRoutine.id ?: return@withTransaction

            //삭제 후 업데이트.
            appDatabase.TimeRoutineDayOfWeekDao().delete(timeRoutineId)
            routineDayOfWeekList.forEach {
                appDatabase.TimeRoutineDayOfWeekDao().delete(it.dayOfWeek)

                appDatabase.TimeRoutineDayOfWeekDao().add(
                    it.toTimeRoutineDayOfWeekSchema(timeRoutineId)
                )
            }
        }
    }
}


