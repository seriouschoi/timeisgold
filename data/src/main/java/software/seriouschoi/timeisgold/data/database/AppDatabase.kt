package software.seriouschoi.timeisgold.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import software.seriouschoi.timeisgold.data.database.dao.TimeScheduleDao
import software.seriouschoi.timeisgold.data.database.dao.TimeScheduleDayOfWeekDao
import software.seriouschoi.timeisgold.data.database.dao.TimeSlotDao
import software.seriouschoi.timeisgold.data.database.dao.TimeSlotMemoDao
import software.seriouschoi.timeisgold.data.database.dao.relation.TimeScheduleRelationDao
import software.seriouschoi.timeisgold.data.database.dao.relation.TimeScheduleWithDayOfWeeksDao
import software.seriouschoi.timeisgold.data.database.dao.relation.TimeSlotRelationDao
import software.seriouschoi.timeisgold.data.database.schema.TimeScheduleDayOfWeekSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeScheduleSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotMemoSchema
import software.seriouschoi.timeisgold.data.database.typeconverter.DayOfWeekConverter
import software.seriouschoi.timeisgold.data.database.typeconverter.LocalTimeConverter

@Database(
    entities = [
        TimeSlotSchema::class,
        TimeSlotMemoSchema::class,
        TimeScheduleSchema::class,
        TimeScheduleDayOfWeekSchema::class
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

    abstract fun TimeScheduleWithDayOfWeeksDao(): TimeScheduleWithDayOfWeeksDao
}