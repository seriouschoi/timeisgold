package software.seriouschoi.timeisgold.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import software.seriouschoi.timeisgold.data.repositories.NewRoutineRepositoryPortAdapter
import software.seriouschoi.timeisgold.data.repositories.NewSlotRepositoryAdapter
import software.seriouschoi.timeisgold.domain.port.NewRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.NewSlotRepositoryPort


@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoriesModule {

    @Binds
    @Singleton
    abstract fun bindNewRoutineRepository(
        impl: NewRoutineRepositoryPortAdapter
    ): NewRoutineRepositoryPort

    @Binds
    @Singleton
    abstract fun bindNewSlotRepository(
        impl: NewSlotRepositoryAdapter
    ) : NewSlotRepositoryPort
}