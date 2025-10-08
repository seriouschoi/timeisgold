package software.seriouschoi.timeisgold.feature.timeroutine.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import software.seriouschoi.timeisgold.feature.timeroutine.data.TimeRoutineSharedState
import javax.inject.Singleton

/**
 * Created by jhchoi on 2025. 10. 6.
 * jhchoi
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class Modules {
    @Binds
    @Singleton()
    abstract fun bindState(): TimeRoutineSharedState
}