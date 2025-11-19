package software.seriouschoi.navigator.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import software.seriouschoi.navigator.DestNavigatorPort
import software.seriouschoi.navigator.DestNavigatorPortAdapter

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class DestNavigatorModule {
    @Binds
    internal abstract fun bindDestNavigator(impl: DestNavigatorPortAdapter): DestNavigatorPort
}