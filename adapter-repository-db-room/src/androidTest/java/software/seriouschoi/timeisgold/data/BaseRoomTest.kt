package software.seriouschoi.timeisgold.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.fixture.TimeSlotTestFixtures
import software.seriouschoi.timeisgold.data.repositories.TimeRoutineRepositoryAdapter
import software.seriouschoi.timeisgold.data.repositories.TimeSlotRepositoryAdapter
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import timber.log.Timber

internal abstract class BaseRoomTest {
    private lateinit var db: AppDatabase

    protected lateinit var timeSlotRepo: TimeSlotRepositoryAdapter
    protected lateinit var timeRoutineRepo: TimeRoutineRepositoryPort
    protected val timeSlotTestFixtures = TimeSlotTestFixtures

    @Before
    fun setupBase() {
        Timber.plant(Timber.DebugTree())

        Timber.d("setupBase")

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        timeSlotRepo = TimeSlotRepositoryAdapter(db)
        timeRoutineRepo = TimeRoutineRepositoryAdapter(db)
    }

    @After
    fun tearDownBase() {
        Timber.d("tearDownBase")
        db.close()
    }
}