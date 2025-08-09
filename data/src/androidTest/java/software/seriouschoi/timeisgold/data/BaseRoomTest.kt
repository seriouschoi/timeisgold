package software.seriouschoi.timeisgold.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.fixture.TimeSlotTestFixtures
import software.seriouschoi.timeisgold.data.repositories.TimeScheduleRepositoryImpl
import software.seriouschoi.timeisgold.data.repositories.TimeSlotRepositoryImpl
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository
import timber.log.Timber
import java.time.DayOfWeek

internal abstract class BaseRoomTest {
    private lateinit var db: AppDatabase

    protected lateinit var timeSlotRepo: TimeSlotRepositoryImpl
    protected lateinit var timeScheduleRepo: TimeScheduleRepository
    protected val timeSlotTestFixtures = TimeSlotTestFixtures

    @Before
    fun setupBase() {
        Timber.plant(Timber.DebugTree())

        Timber.d("setupBase")

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        timeSlotRepo = TimeSlotRepositoryImpl(db)
        timeScheduleRepo = TimeScheduleRepositoryImpl(db)
    }

    @After
    fun tearDownBase() {
        Timber.d("tearDownBase")
        db.close()
    }
}