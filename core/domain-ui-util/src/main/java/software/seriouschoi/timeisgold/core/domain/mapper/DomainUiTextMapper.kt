package software.seriouschoi.timeisgold.core.domain.mapper

import software.seriouschoi.timeisgold.core.common.ui.UiText
import software.seriouschoi.timeisgold.domain.data.DomainError
import software.seriouschoi.timeisgold.core.common.ui.R as CommonR

/**
 * Created by jhchoi on 2025. 9. 9.
 * jhchoi
 */

fun DomainError.toUiText(): UiText = when (this) {
    is DomainError.Validation -> {
        when (this) {
            DomainError.Validation.NoSelectedDayOfWeek -> UiText.MultipleResArgs.create(
                CommonR.string.message_format_select,
                CommonR.string.text_day_of_week
            )

            DomainError.Validation.EmptyTitle -> UiText.MultipleResArgs.create(
                CommonR.string.message_format_empty,
                CommonR.string.text_title

            )

            DomainError.Validation.TitleLength -> UiText.MultipleResArgs.create(
                CommonR.string.message_format_invalid,
                CommonR.string.text_title
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

            DomainError.Conflict.Time -> UiText.MultipleResArgs.create(
                CommonR.string.message_format_not_available_selected_item,
                CommonR.string.text_time
            )
        }
    }

    is DomainError.NotFound -> {
        when (this) {
            DomainError.NotFound.TimeRoutine -> UiText.MultipleResArgs.create(
                CommonR.string.message_format_notfound_data,
                CommonR.string.text_routine
            )

            DomainError.NotFound.TimeSlot -> UiText.MultipleResArgs.create(
                CommonR.string.message_format_notfound_data,
                CommonR.string.text_timeslot
            )
        }
    }

    is DomainError.Technical -> {
        when (this) {
            DomainError.Technical.Unknown -> UiText.Res(
                id = CommonR.string.message_error_tech_unknown
            )
        }
    }
}