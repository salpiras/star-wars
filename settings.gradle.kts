pluginManagement {
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
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "StarWars"
include(":app")
include(":core:network")
include(":core:model")
include(":data:planet")
include(":core:domain")
include(":feature:planet-list")
include(":core:design")
include(":feature:planet-detail")