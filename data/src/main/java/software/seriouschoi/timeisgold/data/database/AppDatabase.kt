package software.seriouschoi.timeisgold.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import software.seriouschoi.timeisgold.data.database.dao.TimeScheduleDao
import software.seriouschoi.timeisgold.data.database.dao.TimeScheduleDayOfWeekDao
import software.seriouschoi.timeisgold.data.database.dao.TimeSlotDao
import software.seriouschoi.timeisgold.data.database.dao.TimeSlotMemoDao
import software.seriouschoi.timeisgold.data.database.dao.relation.TimeScheduleRelationDao
import software.seriouschoi.timeisgold.data.database.dao.relation.TimeSlotRelationDao
import software.seriouschoi.timeisgold.data.database.entities.TimeScheduleDayOfWeekEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeScheduleEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotMemoEntity
import software.seriouschoi.timeisgold.data.database.type_converter.DayOfWeekConverter
import software.seriouschoi.timeisgold.data.database.type_converter.LocalTimeConverter

@Database(
    entities = [
        TimeSlotEntity::class,
        TimeSlotMemoEntity::class,
        TimeScheduleEntity::class,
        TimeScheduleDayOfWeekEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(LocalTimeConverter::class, DayOfWeekConverter::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun TimeScheduleDao(): TimeScheduleDao
    abstract fun TimeScheduleDayOfWeekDao(): TimeScheduleDayOfWeekDao

    abstract fun TimeSlotDao(): TimeSlotDao
    abstract fun TimeSlotMemoDao(): TimeSlotMemoDao

    abstract fun TimeSlotRelationDao(): TimeSlotRelationDao
    abstract fun TimeScheduleRelationDao(): TimeScheduleRelationDao
}