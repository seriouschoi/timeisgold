package software.seriouschoi.timeisgold.core.common.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

sealed interface TigButtonTypes {
    @Composable
    fun containerColor(): Color

    @Composable
    fun onColor(): Color


    object Normal : TigButtonTypes {
        @Composable
        override fun containerColor(): Color = MaterialTheme.colorScheme.secondaryContainer

        @Composable
        override fun onColor(): Color = MaterialTheme.colorScheme.onSecondaryContainer

    }

    object Primary : TigButtonTypes {
        @Composable
        override fun containerColor(): Color = MaterialTheme.colorScheme.primaryContainer

        @Composable
        override fun onColor(): Color = MaterialTheme.colorScheme.onPrimaryContainer
    }
}