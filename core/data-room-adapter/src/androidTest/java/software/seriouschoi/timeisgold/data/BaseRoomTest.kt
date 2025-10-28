package software.seriouschoi.timeisgold.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import software.seriouschoi.timeisgold.data.database.AppDatabase
import software.seriouschoi.timeisgold.data.repositories.TimeRoutineRepositoryPortAdapter
import software.seriouschoi.timeisgold.data.repositories.TimeSlotRepositoryPortAdapter
import timber.log.Timber

internal abstract class BaseRoomTest {
    private lateinit var db: AppDatabase

    protected lateinit var timeSlotRepo: TimeSlotRepositoryPortAdapter
    protected lateinit var timeRoutineRepo: TimeRoutineRepositoryPortAdapter
    protected val testFixtures = TimeRoutineTestFixtures()

    @Before
    fun setupBase() {
        Timber.plant(Timber.DebugTree())

        Timber.d("setupBase")

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        timeSlotRepo = TimeSlotRepositoryPortAdapter(db)
        timeRoutineRepo = TimeRoutineRepositoryPortAdapter(db)
    }

    @After
    fun tearDownBase() {
        Timber.d("tearDownBase")
        db.close()
    }
}