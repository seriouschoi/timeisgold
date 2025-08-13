package software.seriouschoi.timeisgold.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import software.seriouschoi.timeisgold.data.repositories.TimeRoutineRepositoryImpl
import software.seriouschoi.timeisgold.data.repositories.TimeSlotRepositoryImpl
import software.seriouschoi.timeisgold.domain.repositories.TimeRoutineRepository
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository


@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindTimeSlotRepository(
        impl: TimeSlotRepositoryImpl
    ): TimeSlotRepository

    @Binds
    @Singleton
    abstract fun bindTimeRoutineRepository(
        impl: TimeRoutineRepositoryImpl
    ): TimeRoutineRepository

}