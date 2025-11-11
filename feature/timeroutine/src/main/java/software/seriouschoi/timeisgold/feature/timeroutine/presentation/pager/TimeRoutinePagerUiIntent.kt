package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager

import java.time.DayOfWeek

internal sealed interface TimeRoutinePagerUiIntent {
    // TODO: jhchoi 2025. 11. 10. 이게 인텐트이긴 좀 이상한데..
    /*
    아마..페이저로 요일을 바꿨을때 발행하는 인텐트인듯. 이름만 바꾸면 될듯.
    요일 선택이라거나.. 현재 요일 선택이라던가..
    근데 StateIntent를 감싼거니깐.. PagerIntent로 해도 될듯..
    이거 이름좀 고민해보자..
     */
    data class SelectDayOfWeek(val dayOfWeek: DayOfWeek) : TimeRoutinePagerUiIntent
    data class UpdateRoutineTitle(val title: String) : TimeRoutinePagerUiIntent
    data class CheckDayOfWeek(
        val checkedDayOfWeeks: Set<DayOfWeek>,
    ) : TimeRoutinePagerUiIntent
}