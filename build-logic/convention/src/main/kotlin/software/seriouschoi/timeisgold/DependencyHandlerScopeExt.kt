package software.seriouschoi.timeisgold

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope


internal fun DependencyHandlerScope.implementation(libs: VersionCatalog, libAlias: String) {
    "implementation"(dependency = libs.findLibrary(libAlias).get())
}

internal fun DependencyHandlerScope.implementation(
    libs: VersionCatalog,
    bom: Provider<MinimalExternalModuleDependency>
) {
    "implementation"(platform(bom))
}

internal fun DependencyHandlerScope.testImplementation(libs: VersionCatalog, libAlias: String) {
    "testImplementation"(dependency = libs.findLibrary(libAlias).get())
}

internal fun DependencyHandlerScope.androidTestImplementation(libs: VersionCatalog, libAlias: String) {
    "androidTestImplementation"(dependency = libs.findLibrary(libAlias).get())
}

internal fun DependencyHandlerScope.androidTestImplementation(
    libs: VersionCatalog,
    bom: Provider<MinimalExternalModuleDependency>
) {
    "androidTestImplementation"(platform(bom))
}

internal fun DependencyHandlerScope.debugImplementation(libs: VersionCatalog, libAlias: String) {
    "debugImplementation"(dependency = libs.findLibrary(libAlias).get())
}

internal fun DependencyHandlerScope.ksp(libs: VersionCatalog, libAlias: String) {
    "ksp"(dependency = libs.findLibrary(libAlias).get())
}