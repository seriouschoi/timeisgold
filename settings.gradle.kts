pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "timeisgold"
include(":app")
include(":core:domain")
include(":core:domain-data")
include(":core:domain-ui-util")
include(":core:data-room-adapter")
include(":core:navigator-api")
include(":core:navigator-adapter")
include(":core:common-ui")
include(":core:test-util")
include(":core:common-util")
include(":core:android-test-util")
include(":feature:timeroutine")
