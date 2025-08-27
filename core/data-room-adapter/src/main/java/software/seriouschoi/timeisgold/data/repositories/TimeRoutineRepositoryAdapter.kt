package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineEntity
import software.seriouschoi.timeisgold.data.mapper.toTimeRoutineSchemaSchema
import software.seriouschoi.timeisgold.data.mapper.toTimeSlotEntity
import software.seriouschoi.timeisgold.domain.composition.TimeRoutineComposition
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.domain.entities.TimeRoutineEntity
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

internal class TimeRoutineRepositoryAdapter @Inject constructor(
    private val appDatabase: AppDatabase
) : TimeRoutineRepositoryPort {
    override suspend fun addTimeRoutine(timeRoutine: TimeRoutineComposition) {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getTimeRoutine(week: DayOfWeek): Flow<TimeRoutineEntity?> {
        val dao = appDatabase.TimeRoutineJoinDayOfWeekViewDao()
        return dao.getLatestByDayOfWeek(week).distinctUntilChanged().map {
            it?.toTimeRoutineEntity()
        }.distinctUntilChanged()
    }

    override suspend fun setTimeRoutine(composition: TimeRoutineComposition) {
        appDatabase.withTransaction {
            val routineId = appDatabase.TimeRoutineDao().get(composition.timeRoutine.uuid).first()?.id
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


    override suspend fun getTimeRoutineDetailByUuid(timeRoutineUuid: String): TimeRoutineComposition? {
        return appDatabase.withTransaction {
            val routineDto = appDatabase.TimeRoutineDao().get(timeRoutineUuid).first()
            val routineEntity = routineDto?.toTimeRoutineEntity() ?: return@withTransaction null

            val dayOfWeek = appDatabase.TimeRoutineJoinDayOfWeekViewDao().getDayOfWeeksByTimeRoutine(timeRoutineUuid).first()
            val dayOfWeekEntities = dayOfWeek.map {
                TimeRoutineDayOfWeekEntity(
                    dayOfWeek = it
                )
            }

            val timeSlots = appDatabase.TimeRoutineJoinTimeSlotViewDao().getTimeSlotsByTimeRoutine(timeRoutineUuid).first()
            val timeSlotEntities = timeSlots.map {
                it.toTimeSlotEntity()
            }
            return@withTransaction TimeRoutineComposition(
                timeRoutine = routineEntity,
                timeSlots = timeSlotEntities,
                dayOfWeeks = dayOfWeekEntities
            )

        }
    }

    private suspend fun updateDayOfWeekList(
        routineDayOfWeekList: List<TimeRoutineDayOfWeekEntity>,
        timeRoutineUuid: String
    ) {
        appDatabase.withTransaction {
            val timeRoutine = appDatabase.TimeRoutineDao().get(timeRoutineUuid).first() ?: return@withTransaction
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


