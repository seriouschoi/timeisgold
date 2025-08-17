package software.seriouschoi.timeisgold.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import software.seriouschoi.timeisgold.navigation.DestNavigatorPortAdapter
import software.seriouschoi.timeisgold.presentation.navigation.DestNavigatorPort

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class DestNavigatorModule {
    @Binds
    abstract fun bindDestNavigator(impl: DestNavigatorPortAdapter): DestNavigatorPort
}