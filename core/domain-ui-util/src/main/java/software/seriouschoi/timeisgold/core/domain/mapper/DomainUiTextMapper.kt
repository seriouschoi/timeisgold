package software.seriouschoi.timeisgold.core.domain.mapper

import software.seriouschoi.timeisgold.core.common.ui.R as CommonR
import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.domain.data.DomainError

/**
 * Created by jhchoi on 2025. 9. 9.
 * jhchoi
 */

fun DomainError.toUiText(): UiText = when (this) {
    is DomainError.Validation -> {
        when (this) {
            DomainError.Validation.NoSelectedDayOfWeek -> UiText.Res(
                id = CommonR.string.message_error_valid_no_selected_day_of_week
            )

            DomainError.Validation.EmptyTitle -> UiText.Res(
                id = CommonR.string.message_error_valid_empty_title
            )
        }
    }

    is DomainError.Conflict -> {
        when (this) {
            DomainError.Conflict.DayOfWeek -> UiText.Res(
                id = CommonR.string.message_error_conflict_day_of_week
            )

            DomainError.Conflict.Data -> UiText.Res(
                id = CommonR.string.message_error_conflict_data
            )
        }
    }

    is DomainError.NotFound -> {
        when (this) {
            DomainError.NotFound.TimeRoutine -> UiText.MultipleResArgs.create(CommonR.string.message_format_notfound_data, CommonR.string.text_routine)

            DomainError.NotFound.TimeSlot -> UiText.MultipleResArgs.create(CommonR.string.message_format_notfound_data, CommonR.string.text_timeslot)
        }
    }

    is DomainError.Technical -> {
        when(this) {
            DomainError.Technical.Unknown -> UiText.Res(
                id = CommonR.string.message_error_tech_unknown
            )
        }
    }
}