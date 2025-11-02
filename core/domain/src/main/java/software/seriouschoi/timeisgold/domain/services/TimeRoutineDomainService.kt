package software.seriouschoi.timeisgold.domain.services

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 29.
 * jhchoi
 */
class TimeRoutineDomainService @Inject constructor(
    private val routineRepository: TimeRoutineRepositoryPort
){
    suspend fun getOrCreateRoutineId(dayOfWeek: DayOfWeek): DomainResult<String> {
        val routineId = routineRepository.watchRoutine(dayOfWeek).first().let {
            it as? DataResult.Success
        }?.let { it: DataResult.Success<MetaEnvelope<TimeRoutineVO>> ->
            it.value.metaInfo.uuid
        } ?: run {
            routineRepository.setTimeRoutine(
                timeRoutine = TimeRoutineVO(
                    title = "",
                    dayOfWeeks = setOf(dayOfWeek)
                ),
            ).let {
                it as? DataResult.Success
            }?.value?.uuid
        } ?: return DomainResult.Failure(
            DomainError.NotFound.TimeRoutine
        )

        return DomainResult.Success(routineId)
    }
}