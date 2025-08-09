package software.seriouschoi.timeisgold.data.repositories

import androidx.room.withTransaction
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.mapper.toDomain
import software.seriouschoi.timeisgold.data.mapper.toEntity
import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleData
import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleDayOfWeekData
import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleDetailData
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository
import java.time.DayOfWeek
import javax.inject.Inject

internal class TimeScheduleRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : TimeScheduleRepository {
    override suspend fun addTimeSchedule(timeSchedule: TimeScheduleData) {
        appDatabase.withTransaction {
            appDatabase.TimeScheduleDao().add(
                timeSchedule.toEntity()
            )

            updateDayOfWeekList(
                scheduleDayOfWeekList = timeSchedule.dayOfWeekList,
                timeScheduleUuid = timeSchedule.uuid
            )
        }
    }

    override suspend fun getTimeSchedule(week: DayOfWeek): TimeScheduleDetailData? {
        return appDatabase.withTransaction {
            val timeScheduleUuid =
                appDatabase.TimeScheduleRelationDao().getByDayOfWeek(week).maxByOrNull {
                    it.timeSchedule.createTime
                }?.timeSchedule?.uuid ?: return@withTransaction null

            appDatabase.TimeScheduleRelationDao().get(timeScheduleUuid)?.toDomain()
        }
    }


    override suspend fun setTimeSchedule(timeSchedule: TimeScheduleData) {
        appDatabase.withTransaction {
            val scheduleId = appDatabase.TimeScheduleDao().get(timeSchedule.uuid)?.id
                ?: throw IllegalStateException("time schedule is null")
            appDatabase.TimeScheduleDao().update(
                timeSchedule.toEntity(id = scheduleId)
            )

            updateDayOfWeekList(timeSchedule.dayOfWeekList, timeSchedule.uuid)
        }
    }

    override suspend fun deleteTimeSchedule(timeScheduleUuid: String) {
        appDatabase.withTransaction {
            val timeScheduleEntity =
                appDatabase.TimeScheduleDao().get(timeScheduleUuid) ?: return@withTransaction

            // remove time schedule.
            appDatabase.TimeScheduleDao().delete(timeScheduleEntity)
        }
    }


    override suspend fun getTimeScheduleByUuid(timeScheduleUuid: String): TimeScheduleDetailData? {
        return appDatabase.withTransaction {
            appDatabase.TimeScheduleRelationDao().get(timeScheduleUuid)?.toDomain()
        }
    }

    private suspend fun updateDayOfWeekList(
        scheduleDayOfWeekList: List<TimeScheduleDayOfWeekData>,
        timeScheduleUuid: String
    ) {
        appDatabase.withTransaction {
            val timeScheduleRelation =
                appDatabase.TimeScheduleRelationDao().get(timeScheduleUuid)
                    ?: throw IllegalStateException("time schedule relation is null")

            val timeScheduleId = timeScheduleRelation.timeSchedule.id
                ?: throw IllegalStateException("time schedule id is null")

            appDatabase.TimeScheduleDayOfWeekDao().delete(timeScheduleId)
            //새로운 요일들.
            scheduleDayOfWeekList.forEach {
                appDatabase.TimeScheduleDayOfWeekDao().add(
                    it.toEntity(timeScheduleId)
                )
            }
        }
    }
}


