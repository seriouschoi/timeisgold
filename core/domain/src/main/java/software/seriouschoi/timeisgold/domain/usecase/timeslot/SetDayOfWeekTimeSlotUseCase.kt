package software.seriouschoi.timeisgold.domain.usecase.timeslot

import kotlinx.coroutines.flow.first
import software.seriouschoi.timeisgold.core.common.util.MetaInfo
import software.seriouschoi.timeisgold.domain.data.DataResult
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.domain.data.DomainResult
import software.seriouschoi.timeisgold.domain.data.asDomainResult
import software.seriouschoi.timeisgold.domain.data.vo.TimeRoutineVO
import software.seriouschoi.timeisgold.domain.data.vo.TimeSlotVO
import software.seriouschoi.timeisgold.domain.port.NewRoutineRepositoryPort
import software.seriouschoi.timeisgold.domain.port.NewSlotRepositoryPort
import software.seriouschoi.timeisgold.domain.services.TimeSlotDomainService
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * Created by jhchoi on 2025. 10. 27.
 * jhchoi
 */
class SetDayOfWeekTimeSlotUseCase @Inject constructor(
    private val slotRepository: NewSlotRepositoryPort,
    private val routineRepository: NewRoutineRepositoryPort,
    private val timeSlotDomainService: TimeSlotDomainService,
) {
    suspend fun execute(
        dayOfWeek: DayOfWeek,
        timeSlot: TimeSlotVO,
        slotId: String?,
    ): DomainResult<MetaInfo> {
        val policyResult = timeSlotDomainService.isPolicyValid(
            timeSlot
        )
        if (policyResult is DomainResult.Failure) return policyResult

        // TODO: 아래의 과정은 별도의 service로 분리.
        /*
        usecase는 비지니스 로직이 정의되고, 이는 즉 여러 레포지토리나 제약사항들로 요청을 ochestration한다.
        그러다보면, 다른 레포지토리에 저장/불러오기 등을 해야하는데, 이 과정에서 실질적으로 다른 usecase의 동작이 필요해진다.
        허나 usecase가 다른 usecase를 호출하게 되면, 상호 참조를 비롯한 여러 상황으로 참조구조가 모호해질 수 있으므로,
        아래의 루틴 저장 로직을 별개의 서비스 혹은 매니저로 만들어서 의존성을 주입받아 호출해야 한다.
         */
        val routineUuid = routineRepository.watchRoutine(
            dayOfWeek
        ).first().let {
            it as? DataResult.Success
        }?.value?.metaInfo?.uuid ?: run {
            val setRoutineResult = routineRepository.setTimeRoutine(
                timeRoutine = TimeRoutineVO(
                    title = "",
                    dayOfWeeks = setOf(dayOfWeek)
                ),
                routineId = null
            ) as? DataResult.Success

            setRoutineResult?.value?.uuid
        } ?: return DomainResult.Failure(DomainError.Technical.Unknown)

        return slotRepository.setTimeSlot(
            timeSlot = timeSlot,
            slotId = slotId,
            routineId = routineUuid
        ).asDomainResult()
    }
}