package software.seriouschoi.timeisgold.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import software.seriouschoi.timeisgold.data.database.dao.TimeRoutineDao
import software.seriouschoi.timeisgold.data.database.dao.TimeRoutineDayOfWeekDao
import software.seriouschoi.timeisgold.data.database.dao.TimeSlotDao
import software.seriouschoi.timeisgold.data.database.dao.view.TimeRoutineJoinTimeSlotViewDao
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineSchema
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotSchema
import software.seriouschoi.timeisgold.data.database.typeconverter.DayOfWeekConverter
import software.seriouschoi.timeisgold.data.database.typeconverter.LocalTimeConverter
import software.seriouschoi.timeisgold.data.database.view.TimeRoutineJoinTimeSlotView

@Database(
    entities = [
        TimeSlotSchema::class,
        TimeRoutineSchema::class,
        TimeRoutineDayOfWeekSchema::class
    ],
    views = [
        TimeRoutineJoinTimeSlotView::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(LocalTimeConverter::class, DayOfWeekConverter::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun TimeRoutineDao(): TimeRoutineDao
    abstract fun TimeRoutineDayOfWeekDao(): TimeRoutineDayOfWeekDao

    abstract fun TimeSlotDao(): TimeSlotDao

    abstract fun TimeRoutineJoinTimeSlotViewDao(): TimeRoutineJoinTimeSlotViewDao
}