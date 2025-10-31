package software.seriouschoi.timeisgold.data.database.typeconverter

import androidx.room.TypeConverter
import java.time.Instant

/**
 * Created by jhchoi on 2025. 10. 30.
 * jhchoi
 */
class InstantTimeConverter {
    @TypeConverter
    fun toMillis(value: Instant): Long {
        return value.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }
}