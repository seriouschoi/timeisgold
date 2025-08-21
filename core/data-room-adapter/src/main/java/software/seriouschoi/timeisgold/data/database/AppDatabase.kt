package software.seriouschoi.timeisgold.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import software.seriouschoi.timeisgold.data.database.dao.TimeRoutineDao
import software.seriouschoi.timeisgold.data.database.dao.TimeRoutineDayOfWeekDao
import software.seriouschoi.timeisgold.data.database.dao.TimeSlotDao
import software.seriouschoi.timeisgold.data.database.dao.TimeSlotMemoDao
import software.seriouschoi.timeisgold.data.database.dao.relation.TimeRoutineRelationDao
import software.seriouschoi.timeisgold.data.database.dao.relation.TimeRoutineWithDayOfWeeksDao
import software.seriouschoi.timeisgold.data.database.dao.relation.TimeSlotRelationDao
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotMemoSchema
import software.seriouschoi.timeisgold.data.database.typeconverter.DayOfWeekConverter
import software.seriouschoi.timeisgold.data.database.typeconverter.LocalTimeConverter

@Database(
    entities = [
        TimeSlotSchema::class,
        TimeSlotMemoSchema::class,
        TimeRoutineSchema::class,
        TimeRoutineDayOfWeekSchema::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(LocalTimeConverter::class, DayOfWeekConverter::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun TimeRoutineDao(): TimeRoutineDao
    abstract fun TimeRoutineDayOfWeekDao(): TimeRoutineDayOfWeekDao

    abstract fun TimeSlotDao(): TimeSlotDao
    abstract fun TimeSlotMemoDao(): TimeSlotMemoDao

    abstract fun TimeSlotRelationDao(): TimeSlotRelationDao
    abstract fun TimeRoutineRelationDao(): TimeRoutineRelationDao

    abstract fun TimeRoutineWithDayOfWeeksDao(): TimeRoutineWithDayOfWeeksDao
}