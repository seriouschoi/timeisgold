package software.seriouschoi.timeisgold.feature.timeroutine.edit

import java.time.DayOfWeek
import java.util.UUID

internal sealed class TimeRoutineEditUiIntent(
    open val uuid: UUID = UUID.randomUUID(),
) {
    data class Save(
        override val uuid: UUID = UUID.randomUUID(),
    ) : TimeRoutineEditUiIntent()

    data class Cancel(
        override val uuid: UUID = UUID.randomUUID(),
    ) : TimeRoutineEditUiIntent()

    data class Exit(
        override val uuid: UUID = UUID.randomUUID(),
    ) : TimeRoutineEditUiIntent()

    data class SaveConfirm(
        override val uuid: UUID = UUID.randomUUID(),
    ) : TimeRoutineEditUiIntent()

    data class UpdateRoutineTitle(
        val title: String,
    ) : TimeRoutineEditUiIntent()

    data class UpdateDayOfWeek(
        val dayOfWeek: DayOfWeek, val checked: Boolean,
    ) : TimeRoutineEditUiIntent()
}