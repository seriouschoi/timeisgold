package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import software.seriouschoi.timeisgold.core.common.util.runSuspendCatching
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.di.ApplicationScope
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotSchema
import software.seriouschoi.timeisgold.data.util.asDataResult
import software.seriouschoi.timeisgold.domain.data.DataError
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineDefinition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import timber.log.Timber
import java.time.DayOfWeek
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class TimeRoutineRepositoryPortAdapter @Inject constructor(
    private val appDatabase: AppDatabase,
    @ApplicationScope private val stateScope: CoroutineScope
) : TimeRoutineRepositoryPort {

    override suspend fun saveTimeRoutineComposition(composition: TimeRoutineComposition): DataResult<String> {
        return runSuspendCatching {
            appDatabase.withTransaction {
                appDatabase.TimeRoutineDao().upsert(composition.timeRoutine)

                updateTimeSlots(
                    incomingSlots = composition.timeSlots,
                    routineUuid = composition.timeRoutine.uuid
                )
                updateDayOfWeekList(composition.dayOfWeeks, composition.timeRoutine.uuid)
            }
            composition.timeRoutine.uuid
        }.asDataResult()
    }


    override fun watchCompositionByDayOfWeek(dayOfWeek: DayOfWeek): Flow<TimeRoutineComposition?> {
        return appDatabase.TimeRoutineJoinDayOfWeekViewDao()
            .watchLatestByDayOfWeek(dayOfWeek)
            .distinctUntilChanged()
            .map {
                it?.toTimeRoutineEntity()
            }
            .flatMapLatest { routine: TimeRoutineEntity? ->
                if (routine == null) flowOf(null)
                else {
                    combine(
                        observeWeeks(routine.uuid),
                        watchSlots(routine.uuid)
                    ) { weeks, slots ->
                        TimeRoutineComposition(
                            timeRoutine = routine,
                            dayOfWeeks = weeks.toSet(),
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
            .distinctUntilChanged()
            .map { dayOfWeeks: List<DayOfWeek> ->
                Timber.d("observeWeeks - routineUuid=$routineUuid, dayOfWeeks=$dayOfWeeks")
                dayOfWeeks.map {
                    it.toTimeRoutineDayOfWeekEntity()
                }.sortedBy {
                    it.dayOfWeek
                }
            }
            .distinctUntilChanged()

    private fun watchSlots(routineUuid: String): Flow<List<TimeSlotEntity>> =
        appDatabase.TimeRoutineJoinTimeSlotViewDao()
            .watchTimeSlotsByTimeRoutine(routineUuid)
            .distinctUntilChanged()
            .map { list ->
                Timber.d("watchSlots - routineUuid=$routineUuid, list.size=${list.size}")
                list.map {
                    it.toTimeSlotEntity()
                }.sortedBy { it.startTime }
            }
            .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeTimeRoutineByDayOfWeek(day: DayOfWeek): Flow<TimeRoutineEntity?> {
        val dao = appDatabase.TimeRoutineJoinDayOfWeekViewDao()
        return dao.watchLatestByDayOfWeek(day).distinctUntilChanged().map {
            it?.toTimeRoutineEntity()
        }.distinctUntilChanged()
    }

    override suspend fun setTimeSlotList(
        routineUuid: String,
        incomingSlots: List<TimeSlotEntity>,
    ): DataResult<Unit> = runSuspendCatching {
        appDatabase.withTransaction {
            val routineDao = appDatabase.TimeRoutineDao()
            val slotDao = appDatabase.TimeSlotDao()
            val slotJoinRoutineDao = appDatabase.TimeRoutineJoinTimeSlotViewDao()

            val routineId = routineDao.get(routineUuid)?.id
                ?: throw IllegalStateException("time routine is null")

            val existingSlots = slotJoinRoutineDao
                .getTimeSlotsByTimeRoutine(routineUuid).associateBy { it.timeSlotUuid }

            //delete
            val incomingSlotsUuids = incomingSlots.map { it.uuid }.toSet()
            val toDelete = existingSlots.keys - incomingSlotsUuids //기존 slot중 새로 교체될 slot들에 없으면 삭제.
            toDelete.forEach {
                slotDao.delete(it)
            }

            //slots upsert
            incomingSlots.forEach { incomingSlot ->
                val slotId = existingSlots[incomingSlot.uuid]?.timeSlotId
                slotDao.upsert(
                    incomingSlot.toTimeSlotSchema(
                        timeRoutineId = routineId,
                        timeSlotId = slotId
                    )
                )
            }
        }
    }.asDataResult()

    private suspend fun updateTimeSlots(
        incomingSlots: List<TimeSlotEntity>,
        routineUuid: String,
    ) {
        appDatabase.withTransaction {
            val routineDao = appDatabase.TimeRoutineDao()
            val slotDao = appDatabase.TimeSlotDao()
            val slotJoinRoutineDao = appDatabase.TimeRoutineJoinTimeSlotViewDao()

            val routineId =
                routineDao.get(routineUuid)?.id
                    ?: throw IllegalStateException("time routine is null")

            val existingSlots = slotJoinRoutineDao
                .getTimeSlotsByTimeRoutine(routineUuid).associateBy { it.timeSlotUuid }

            //delete
            val incomingSlotsUuids = incomingSlots.map { it.uuid }.toSet()
            val toDelete = existingSlots.keys - incomingSlotsUuids //기존 slot중 새로 교체될 slot들에 없으면 삭제.
            toDelete.forEach {
                slotDao.delete(it)
            }

            //slots upsert
            incomingSlots.forEach { incomingSlot ->
                val slotId = existingSlots[incomingSlot.uuid]?.timeSlotId
                slotDao.upsert(
                    incomingSlot.toTimeSlotSchema(
                        timeRoutineId = routineId,
                        timeSlotId = slotId
                    )
                )
            }
        }
    }

    override suspend fun deleteTimeRoutine(timeRoutineUuid: String): DataResult<Unit> {
        val routineDao = appDatabase.TimeRoutineDao()
        return appDatabase.withTransaction {
            val routineSchema = routineDao.get(timeRoutineUuid)
                ?: return@withTransaction DataResult.Failure(DataError.NotFound)

            val deletedCount = routineDao.delete(routineSchema)
            if (deletedCount != 1) return@withTransaction DataResult.Failure(DataError.Conflict)

            return@withTransaction DataResult.Success(Unit)
        }
    }


    override val allRoutinesDayOfWeeks: StateFlow<DataResult<List<DayOfWeek>>> =
        appDatabase.TimeRoutineJoinDayOfWeekViewDao().watchAllDayOfWeeks().map {
            runSuspendCatching {
                it
            }.asDataResult()
        }.stateIn(
            scope = stateScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DataResult.Failure(DataError.NotFound)
        )


    override suspend fun saveTimeRoutineDefinition(routine: TimeRoutineDefinition): DataResult<String> {
        return withContext(Dispatchers.IO) {
            runSuspendCatching {
                appDatabase.withTransaction {
                    appDatabase.TimeRoutineDao().upsert(routine.timeRoutine)
                    updateDayOfWeekList(routine.dayOfWeeks, routine.timeRoutine.uuid)
                    routine.timeRoutine.uuid
                }
            }.asDataResult()
        }
    }

    override fun observeTimeRoutineDefinitionByDayOfWeek(dayOfWeek: DayOfWeek): Flow<TimeRoutineDefinition?> {
        return appDatabase.TimeRoutineJoinDayOfWeekViewDao()
            .watchLatestByDayOfWeek(dayOfWeek)
            .distinctUntilChanged()
            .map {
                it?.toTimeRoutineEntity()
            }
            .flatMapLatest { routine ->
                if (routine == null) flowOf(null)
                else {
                    combine(
                        observeWeeks(routine.uuid),
                        watchSlots(routine.uuid)
                    ) { weeks, slots ->
                        TimeRoutineDefinition(
                            timeRoutine = routine,
                            dayOfWeeks = weeks.toSet(),
                        )
                    }
                }
            }
    }

    override suspend fun getAllTimeRoutineDefinitions(): List<TimeRoutineDefinition> {
        val allTimeRoutines = withContext(Dispatchers.IO) {
            appDatabase.TimeRoutineJoinDayOfWeekViewDao().getAll().groupBy {
                it.toTimeRoutineEntity()
            }
        }
        return allTimeRoutines.keys.map {
            TimeRoutineDefinition(
                timeRoutine = it,
                dayOfWeeks = allTimeRoutines[it]?.map {
                    it.toTimeRoutineDayOfWeekEntity()
                }?.toSet() ?: emptySet()
            )
        }
    }

    override suspend fun getAllDayOfWeeks(): List<DayOfWeek> {
        return appDatabase.TimeRoutineJoinDayOfWeekViewDao().getAllDayOfWeeks()
    }

    override fun observeCompositionByUuidFlow(timeRoutineUuid: String): Flow<TimeRoutineComposition?> {
        return appDatabase.TimeRoutineDao()
            .observe(timeRoutineUuid)
            .distinctUntilChanged()
            .map {
                it?.toTimeRoutineEntity()
            }
            .flatMapLatest {
                if (it == null) flowOf(null)
                else {
                    combine(
                        observeWeeks(it.uuid),
                        watchSlots(it.uuid)
                    ) { weeks, slots ->
                        TimeRoutineComposition(
                            timeRoutine = it,
                            dayOfWeeks = weeks.toSet(),
                            timeSlots = slots
                        )
                    }
                }
            }
            .distinctUntilChanged()
    }

    override suspend fun getCompositionByUuid(timeRoutineUuid: String): TimeRoutineComposition? {
        //flow의 각종 정책을 data모듈 내부에서 처리후 리턴. usecase까지 전파되지 않게 함.
        return observeCompositionByUuidFlow(timeRoutineUuid).first()
    }

    private suspend fun updateDayOfWeekList(
        routineDayOfWeekList: Set<TimeRoutineDayOfWeekEntity>,
        timeRoutineUuid: String,
    ) {
        appDatabase.withTransaction {
            val timeRoutine =
                appDatabase.TimeRoutineDao().observe(timeRoutineUuid).first()
                    ?: return@withTransaction
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


