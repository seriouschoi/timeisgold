package software.seriouschoi.timeisgold.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import software.seriouschoi.timeisgold.core.common.util.CurrentDayOfWeekProviderPort
import software.seriouschoi.timeisgold.core.common.util.SystemCurrentDayOfWeeksProviderAdapter

/**
 * Created by jhchoi on 2025. 11. 19.
 * jhchoi
 */
@Module
@InstallIn(SingletonComponent::class)
object TimeModule {

    @Provides
    fun provideDayOfWeekProvider() : CurrentDayOfWeekProviderPort {
        return SystemCurrentDayOfWeeksProviderAdapter()
    }
}