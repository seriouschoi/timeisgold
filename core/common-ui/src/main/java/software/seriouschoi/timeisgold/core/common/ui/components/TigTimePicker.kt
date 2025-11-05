package software.seriouschoi.timeisgold.core.common.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import software.seriouschoi.timeisgold.core.common.ui.TigThemePreview
import java.time.LocalTime

/**
 * Created by jhchoi on 2025. 11. 2.
 * jhchoi
 */
@Composable
fun TigTimePicker(
    time: LocalTime,
    modifier: Modifier = Modifier,
    timeRange: Pair<LocalTime, LocalTime> = LocalTime.MIN to LocalTime.MAX,
    onChangeTime: (LocalTime) -> Unit
) {
    // TODO: jhchoi 2025. 11. 5. timeRange...
    /*
    예시. 22:00 ~ 03:00
    start range...어.. 22, 23.. 00, 01, 02는 선택되야 하지 않나..?
    분도 마찬가지네..
    분은 더 골치 아프네.

    22:20 ~ 03:10
    이 범위만 허용한다면..
    선택 가능한 hour는 22 ~ 03
    minute은 가변이구나.. 선택된 hour에 따라서 제약이네..
    22일때는 20 ~ 59
    03일때는 00 ~ 10

    range니깐.. 22 ~ 03 입력은 가능 할듯.
    자동으로 24로 나눈 나머지 값이 아니고..
     */

    /*
    자 시작시간부터 순회를 돌리자..
    timeRange.first.hour 부터
     */
    timeRange.first.hour


    Row(
        modifier = modifier
    ) {
        //hour
        TigNumberPickerView(
            value = time.hour,
            range = timeRange.first.hour..timeRange.second.hour,
        ) {
            onChangeTime.invoke(LocalTime.of(it, time.minute))
        }

        //minute
        TigNumberPickerView(
            value = time.minute,
            range = timeRange.first.minute..timeRange.second.minute,
        ) {
            onChangeTime.invoke(LocalTime.of(time.hour, it))
        }
    }
}

@Composable
@TigThemePreview
private fun Preview() {
    TigTimePicker(
        time = LocalTime.of(10, 0),
        timeRange = LocalTime.of(22, 0) to LocalTime.of(3, 59)
    ) {

    }
}