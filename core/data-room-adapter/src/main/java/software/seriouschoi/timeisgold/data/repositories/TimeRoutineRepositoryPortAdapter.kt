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
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineSchema
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotSchema
import software.seriouschoi.timeisgold.domain.data.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.data.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import timber.log.Timber
import java.time.DayOfWeek
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class TimeRoutineRepositoryPortAdapter @Inject constructor(
    private val appDatabase: AppDatabase
) : TimeRoutineRepositoryPort {
    override suspend fun addTimeRoutineComposition(timeRoutine: TimeRoutineComposition) {
        appDatabase.withTransaction {
            val timeRoutineId = appDatabase.TimeRoutineDao().add(
                timeRoutine.timeRoutine.toTimeRoutineSchema(null)
            )

            timeRoutine.timeSlots.forEach {
                appDatabase.TimeSlotDao().insert(
                    it.toTimeSlotSchema(
                        timeRoutineId = timeRoutineId
                    )
                )
            }

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

    private fun observeSlots(routineUuid: String): Flow<List<TimeSlotEntity>> =
        appDatabase.TimeRoutineJoinTimeSlotViewDao()
            .getTimeSlotsByTimeRoutine(routineUuid)
            .distinctUntilChanged()
            .map { list ->
                Timber.d("observeSlots - routineUuid=$routineUuid, list=$list")
                list.map {
                    it.toTimeSlotEntity()
                }.sortedBy { it.startTime }
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
                composition.timeRoutine.toTimeRoutineSchema(id = routineId)
            )

            //slots delete
            updateTimeSlot(
                timeSlots = composition.timeSlots,
                routineUuid = composition.timeRoutine.uuid,
            )
            updateDayOfWeekList(composition.dayOfWeeks, composition.timeRoutine.uuid)
        }
    }

    private suspend fun updateTimeSlot(
        timeSlots: List<TimeSlotEntity>,
        routineUuid: String,
    ) {
        val routineId =
            appDatabase.TimeRoutineDao().get(routineUuid).first()?.id ?: return
        val slotsFromDB = appDatabase.TimeRoutineJoinTimeSlotViewDao()
            .getTimeSlotsByTimeRoutine(routineUuid).first()

        val timeSlotsUuidList = timeSlots.map { it.uuid }
        slotsFromDB.filter { slot ->
            !timeSlotsUuidList.contains(slot.timeSlotUuid)
        }.forEach {
            appDatabase.TimeSlotDao().delete(it.timeSlotUuid)
        }

        //slots update
        timeSlots.forEach { slotForUpdate ->
            val timeSlotDao = appDatabase.TimeSlotDao()
            val slotId = slotsFromDB.find { it.timeSlotUuid == slotForUpdate.uuid }?.timeSlotId
            if (slotId != null) {
                timeSlotDao.update(
                    slotForUpdate.toTimeSlotSchema(
                        timeRoutineId = routineId,
                        timeSlotId = slotId
                    )
                )
            } else {
                timeSlotDao.insert(slotForUpdate.toTimeSlotSchema(routineId))
            }
        }
    }

    override suspend fun deleteTimeRoutine(timeRoutineUuid: String) {
        appDatabase.withTransaction {
            val timeRoutineSchema =
                appDatabase.TimeRoutineDao().get(timeRoutineUuid).first() ?: return@withTransaction

            // remove time routine.
            val deletedCount = appDatabase.TimeRoutineDao().delete(timeRoutineSchema)
            Timber.d("deletedCount: $deletedCount")
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
                it?.toTimeRoutineEntity()
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
                            dayOfWeeks = weeks.toSet(),
                            timeSlots = slots
                        )
                    }
                }
            }
            .distinctUntilChanged()
    }

    private suspend fun updateDayOfWeekList(
        routineDayOfWeekList: Set<TimeRoutineDayOfWeekEntity>,
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


