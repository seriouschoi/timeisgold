package software.seriouschoi.timeisgold.domain.port

import kotlinx.coroutines.flow.Flow
import software.seriouschoi.timeisgold.core.common.util.MetaEnvelope
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import java.time.DayOfWeek

/**
 * Created by jhchoi on 2025. 10. 28.
 * jhchoi
 */
interface NewRoutineRepositoryPort {

    fun setTimeRoutine(
        timeRoutine: TimeRoutineVO,
        routineId: String?
    ): DataResult<MetaInfo>

    fun watchRoutine(
        dayOfWeek: DayOfWeek
    ): Flow<DataResult<MetaEnvelope<TimeRoutineVO>>>

    fun deleteRoutine(
        routineId: String
    ): DataResult<Unit>
}