package software.seriouschoi.timeisgold.feature.timeroutine.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchRoutineUseCase
import software.seriouschoi.timeisgold.domain.usecase.timeroutine.WatchSelectableDayOfWeeksUseCase
import software.seriouschoi.timeisgold.feature.timeroutine.data.TimeRoutineFeatureState
import javax.inject.Singleton

/**
 * Created by jhchoi on 2025. 10. 6.
 * jhchoi
 */
@Module
@InstallIn(SingletonComponent::class)
internal class StateModule {
    @Provides
    @Singleton()
    fun provideState(
        watchRoutineUseCase: WatchRoutineUseCase,
        watchSelectableDayOfWeeksUseCase: WatchSelectableDayOfWeeksUseCase,
    ): TimeRoutineFeatureState {
        return TimeRoutineFeatureState(
            watchRoutineUseCase = watchRoutineUseCase,
            watchSelectableDayOfWeeksUseCase = watchSelectableDayOfWeeksUseCase
        )
    }
}