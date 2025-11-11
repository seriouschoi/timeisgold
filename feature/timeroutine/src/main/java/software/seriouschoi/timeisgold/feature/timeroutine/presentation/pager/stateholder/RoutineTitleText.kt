package software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.stateholder

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import software.seriouschoi.timeisgold.core.common.ui.R
import software.seriouschoi.timeisgold.core.common.ui.components.TigSingleLineTextField
import software.seriouschoi.timeisgold.feature.timeroutine.presentation.pager.TimeRoutinePagerUiIntent
import timber.log.Timber

@Composable
internal fun RoutineTitleText(titleState: RoutineTitleState, onValueChage: (String) -> Unit) {
    Timber.Forest.d("TitleText - titleState=$titleState")
    TigSingleLineTextField(
        value = titleState.title,
        onValueChange = onValueChage,
        modifier = Modifier.Companion.fillMaxWidth(),
        hint = stringResource(R.string.text_routine_title)
    )
}