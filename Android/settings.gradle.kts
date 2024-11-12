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
include(":feat:login")
include(":demo")
include(":feat:storage")
include(":feat:display")
include(":feat:mypage")
