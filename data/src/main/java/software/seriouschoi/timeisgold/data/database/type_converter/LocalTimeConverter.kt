package software.seriouschoi.timeisgold.data.database.type_converter

import androidx.room.TypeConverter
import java.time.LocalTime

internal class LocalTimeConverter {
    @TypeConverter
    fun fromLocalTime(value: LocalTime): Int {
        return value.toSecondOfDay()
    }

    @TypeConverter
    fun toLocalTime(value: Int): LocalTime {
        return LocalTime.ofSecondOfDay(value.toLong())
    }
}