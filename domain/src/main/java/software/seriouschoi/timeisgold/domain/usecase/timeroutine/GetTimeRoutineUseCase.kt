package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import software.seriouschoi.timeisgold.domain.data.timeroutine.TimeRoutineDetailData
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

class GetTimeRoutineUseCase @Inject constructor(
    private val timeRoutineRepositoryPort: TimeRoutineRepositoryPort
) {
    suspend operator fun invoke(week: DayOfWeek) : TimeRoutineDetailData? {
        return timeRoutineRepositoryPort.getTimeRoutineDetail(week)
    }
}