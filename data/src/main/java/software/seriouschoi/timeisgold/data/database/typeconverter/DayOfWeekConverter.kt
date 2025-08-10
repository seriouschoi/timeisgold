package software.seriouschoi.timeisgold.data.database.typeconverter

import androidx.room.TypeConverter
import java.time.DayOfWeek

internal class DayOfWeekConverter {
    @TypeConverter
    fun fromDayOfWeek(value: DayOfWeek): String {
        return value.name
    }

    @TypeConverter
    fun dayOfWeekToString(value: String): DayOfWeek? {
        return DayOfWeek.entries.find {
            it.name == value
        }
    }
}