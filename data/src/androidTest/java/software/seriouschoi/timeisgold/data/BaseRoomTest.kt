package software.seriouschoi.timeisgold.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import software.seriouschoi.timeisgold.data.database.AppDatabase
import timber.log.Timber

internal abstract class BaseRoomTest {
    protected lateinit var db: AppDatabase

    @Before
    fun setupBase() {
        Timber.plant(Timber.DebugTree())

        Timber.d("setupBase")

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDownBase() {
        Timber.d("tearDownBase")
        db.close()
    }
}