package software.seriouschoi.timeisgold.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import software.seriouschoi.timeisgold.data.database.dao.TimeSlotDao
import software.seriouschoi.timeisgold.data.database.dao.TimeSlotWithExtrasRelationDao
import software.seriouschoi.timeisgold.data.database.dao.TimeSlotMemoDao
import software.seriouschoi.timeisgold.data.database.dao.TimeSlot_TimeSlotMemo_Dao
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotMemoEntity
import software.seriouschoi.timeisgold.data.database.entities.TimeSlot_TimeSlotMemoInfo_Entity
import software.seriouschoi.timeisgold.data.database.type_converter.LocalTimeConverter

@Database(
    entities = [
        TimeSlotEntity::class,
        TimeSlotMemoEntity::class,
        TimeSlot_TimeSlotMemoInfo_Entity::class,
    ],
    version = 1,
)
@TypeConverters(LocalTimeConverter::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun TimeSlotDao(): TimeSlotDao
    abstract fun TimeSlotMemoDao(): TimeSlotMemoDao
    abstract fun TimeSlotWithExtrasRelationDao(): TimeSlotWithExtrasRelationDao
    abstract fun TimeSlot_TimeSlotMemo_Dao(): TimeSlot_TimeSlotMemo_Dao

}