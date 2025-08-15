package software.seriouschoi.timeisgold.domain.usecase.timeslot

import software.seriouschoi.timeisgold.domain.data.timeslot.TimeSlotDetailData
import software.seriouschoi.timeisgold.domain.repositories.TimeSlotRepository
import javax.inject.Inject

class GetTimeSlotDetailUseCase @Inject constructor(
    private val timeslotRepository: TimeSlotRepository
) {
    suspend operator fun invoke(timeslotUuid: String): TimeSlotDetailData? {
        return timeslotRepository.getTimeSlotDetail(timeslotUuid = timeslotUuid)
    }
}