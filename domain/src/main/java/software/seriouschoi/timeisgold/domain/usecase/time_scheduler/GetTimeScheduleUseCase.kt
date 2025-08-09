package software.seriouschoi.timeisgold.domain.usecase.time_scheduler

import software.seriouschoi.timeisgold.domain.data.time_schedule.TimeScheduleDetailData
import software.seriouschoi.timeisgold.domain.repositories.TimeScheduleRepository
import java.time.DayOfWeek

class GetTimeScheduleUseCase (
    private val timeScheduleRepository: TimeScheduleRepository
) {
    suspend operator fun invoke(week: DayOfWeek) : TimeScheduleDetailData? {
        return timeScheduleRepository.getTimeSchedule(week)
    }

    // TODO: 요청을 요일별로 할수도 있지만.. 반대로 요일로 설정된 데이터가 있어야하지 않나..?
    /*
    그러니깐 한 시간표를 여러 요일로 쓸 수도 있잖아.
    그걸 고려하면..timeschedule이라는 테이블이 있고..
    timeschdule은 요일테이블과 관계를 가지겠지?
    그리고 timeslot은 timeschdule과 관계를 가질거고..?
    즉 아래의 테이블이 필요한건가..?
    TimeSchedule
    DayOfWeek
    TimeScheduel_DayOfWeek_Relation

    TimeSchedule_TimeSlot_Relation

    그렇다면..timeschedule은 timeslot list말고, dayofweek list도 있어야겠네..(set이 더 맞지만..)

    그리고 mapper도 하나 만들어야 하네, dayOfWeek를 변환할 mapper하나..

    이거 반대로...한 요일에 여러 시간표가 있을수는 없네.
    다시 정리해보자. 타임스케줄은 여러 요일일 수는 있음.
    그런데
     */
}