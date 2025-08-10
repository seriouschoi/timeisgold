package software.seriouschoi.timeisgold.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import software.seriouschoi.timeisgold.domain.policy.TimeSlotPolicy
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository
import software.seriouschoi.timeisgold.domain.usecase.timeslot.SetTimeSlotUseCase

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Provides
    fun provideSetTimeSlotUseCase(
        timeRepository: TimeSlotRepository,
        timeScheduleRepository: TimeScheduleRepository,
        timeSlotPolicy: TimeSlotPolicy
    ): SetTimeSlotUseCase {
        return SetTimeSlotUseCase(
            timeslotRepository = timeRepository,
            timeScheduleRepository = timeScheduleRepository,
            timeslotPolicy = timeSlotPolicy
        )
    }
}