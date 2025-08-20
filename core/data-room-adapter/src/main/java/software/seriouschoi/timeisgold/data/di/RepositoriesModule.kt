package software.seriouschoi.timeisgold.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import software.seriouschoi.timeisgold.data.repositories.TimeRoutineRepositoryAdapter
import software.seriouschoi.timeisgold.data.repositories.TimeSlotRepositoryAdapter
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort


@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindTimeSlotRepository(
        impl: TimeSlotRepositoryAdapter
    ): TimeSlotRepositoryPort

    @Binds
    @Singleton
    abstract fun bindTimeRoutineRepository(
        impl: TimeRoutineRepositoryAdapter
    ): TimeRoutineRepositoryPort

}