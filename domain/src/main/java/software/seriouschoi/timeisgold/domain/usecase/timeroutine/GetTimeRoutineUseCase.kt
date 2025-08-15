package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDetailData
import software.seriouschoi.timeisgold.domain.repositories.TimeRoutineRepository
import java.time.DayOfWeek
import javax.inject.Inject

class GetTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepository: TimeRoutineRepository
) {
    suspend operator fun invoke(week: DayOfWeek) : TimeRoutineDetailData? {
        return timeRoutineRepository.getTimeRoutineDetail(week)
    }
}