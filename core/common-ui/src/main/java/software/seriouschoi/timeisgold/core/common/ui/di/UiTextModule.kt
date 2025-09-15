package software.seriouschoi.timeisgold.core.common.ui.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import software.seriouschoi.timeisgold.core.common.ui.provider.UiTextProvider
import software.seriouschoi.timeisgold.core.common.ui.provider.UiTextResolver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class UiTextModule {
    @Singleton
    @Provides
    fun provider(
        @ApplicationContext context: Context,
    ): UiTextResolver {
        return UiTextProvider(context)
    }

}