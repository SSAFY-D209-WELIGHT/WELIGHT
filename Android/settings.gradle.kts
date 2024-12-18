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

rootProject.name = "WeLight"
include(":app")

gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))
include(":core:ui")
include(":core:network")
include(":core:datastore")
include(":feature:storage")
include(":feature:editor")
include(":feature:detail")
include(":feature:group")
include(":feature:login")
include(":demo")
include(":feature:board")
include(":feature:mypage")
include(":feature:websocketclient")
include(":core:websocket")
include(":audio-test")
include(":audio-test")
include(":core:audio")
