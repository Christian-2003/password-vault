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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PasswordVault"
include(":app")
include(":core:ui")
include(":core:security")
include(":feature:accounts")
include(":feature:auth")
include(":core:common")
include(":data:accounts")
include(":feature:autofill")
include(":feature:search")
include(":data:database")
include(":data:files")
include(":feature:files")
include(":feature:analysis")
include(":feature:export")
