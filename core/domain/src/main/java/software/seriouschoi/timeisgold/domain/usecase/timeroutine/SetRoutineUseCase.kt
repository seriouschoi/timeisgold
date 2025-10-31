package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.port.NewRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 28.
 * jhchoi
 */
class SetRoutineUseCase @Inject constructor(
    private val routineRepository: NewRoutineRepositoryPort,
) {
    suspend fun invoke(
        routineVO: TimeRoutineVO,
        dayOfWeek: DayOfWeek
    ): DomainResult<MetaInfo> {
        val routineDataResult =
            routineRepository.watchRoutine(dayOfWeek).first() as? DataResult.Success

        val routineMetaInfo = routineDataResult?.value?.metaInfo

        return routineRepository.setTimeRoutine(
            routineVO,
            routineId = routineMetaInfo?.uuid
        ).asDomainResult()
    }
}