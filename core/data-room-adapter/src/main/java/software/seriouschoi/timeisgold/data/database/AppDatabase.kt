package software.seriouschoi.timeisgold.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import software.seriouschoi.timeisgold.data.database.dao.TimeRoutineDao
import software.seriouschoi.timeisgold.data.database.dao.TimeRoutineDayOfWeekDao
import software.seriouschoi.timeisgold.data.database.dao.TimeSlotDao
import software.seriouschoi.timeisgold.data.database.dao.view.TimeRoutineJoinTimeSlotViewDao
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineDayOfWeekEntity
import software.seriouschoi.timeisgold.data.database.schema.TimeRoutineEntity
import software.seriouschoi.timeisgold.data.database.schema.TimeSlotEntity
import software.seriouschoi.timeisgold.data.database.typeconverter.DayOfWeekConverter
import software.seriouschoi.timeisgold.data.database.typeconverter.InstantTimeConverter
import software.seriouschoi.timeisgold.data.database.typeconverter.LocalTimeConverter
import software.seriouschoi.timeisgold.data.database.view.TimeRoutineJoinTimeSlotView

@Database(
    entities = [
        TimeSlotEntity::class,
        TimeRoutineEntity::class,
        TimeRoutineDayOfWeekEntity::class
    ],
    views = [
        TimeRoutineJoinTimeSlotView::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(LocalTimeConverter::class, DayOfWeekConverter::class, InstantTimeConverter::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun TimeRoutineDao(): TimeRoutineDao
    abstract fun TimeRoutineDayOfWeekDao(): TimeRoutineDayOfWeekDao

    abstract fun TimeSlotDao(): TimeSlotDao

    abstract fun TimeRoutineJoinTimeSlotViewDao(): TimeRoutineJoinTimeSlotViewDao
}