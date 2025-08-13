package software.seriouschoi.timeisgold.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.repositories.TimeRoutineRepository
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Provides
    fun provideSetTimeSlotUseCase(
        timeRepository: TimeSlotRepository,
        timeRoutineRepository: TimeRoutineRepository,
        timeSlotPolicy: TimeSlotPolicy
    ): SetTimeSlotUseCase {
        return SetTimeSlotUseCase(
            timeslotRepository = timeRepository,
            timeRoutineRepository = timeRoutineRepository,
            timeslotPolicy = timeSlotPolicy
        )
    }
}