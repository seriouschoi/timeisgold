package software.seriouschoi.timeisgold.data.database.type_converter

import androidx.room.TypeConverter
import java.time.LocalTime

internal class LocalTimeConverter {
    @TypeConverter
    fun fromLocalTime(value: LocalTime): Long {
        return value.toNanoOfDay()
    }

    @TypeConverter
    fun toLocalTime(value: Long): LocalTime {
        return LocalTime.ofNanoOfDay(value)
    }
}