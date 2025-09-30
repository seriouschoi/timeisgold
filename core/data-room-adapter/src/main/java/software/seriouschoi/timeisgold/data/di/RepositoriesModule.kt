package software.seriouschoi.timeisgold.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import software.seriouschoi.timeisgold.data.repositories.TimeRoutineRepositoryPortAdapter
import software.seriouschoi.timeisgold.data.repositories.TimeSlotRepositoryPortAdapter
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.TimeSlotRepositoryPort


@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindTimeSlotRepository(
        impl: TimeSlotRepositoryPortAdapter
    ): TimeSlotRepositoryPort

    @Binds
    @Singleton
    abstract fun bindTimeRoutineRepository(
        impl: TimeRoutineRepositoryPortAdapter
    ): TimeRoutineRepositoryPort

}