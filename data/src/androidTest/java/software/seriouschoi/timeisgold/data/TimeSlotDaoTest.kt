package software.seriouschoi.timeisgold.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.database.dao.TimeSlotDao
import software.seriouschoi.timeisgold.data.database.entities.TimeSlotEntity
import timber.log.Timber
import java.time.LocalTime
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class TimeSlotDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: TimeSlotDao

    @Before
    fun setup() {
        Timber.plant(Timber.DebugTree())
        
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = db.TimeSlotDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndQuery() {
        val testUuid = UUID.randomUUID().toString()
        val timeSlotEntity = TimeSlotEntity(
            uuid = testUuid,
            title = "new time slot title",
            startTime = LocalTime.now().minusHours(1),
            endTime = LocalTime.now(),
            createTime = System.currentTimeMillis()
        )
        val newTimeSlotId = db.TimeSlotDao().insert(
            timeSlotEntity
        )
        val compareTimeSlotId = dao.getId(testUuid)
        Timber.d("newTimeSlotId=$newTimeSlotId, compareTimeSlotId=$compareTimeSlotId")
        assert(newTimeSlotId == compareTimeSlotId)
    }
}