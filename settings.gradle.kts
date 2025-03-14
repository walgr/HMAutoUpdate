pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/nexus/content/repositories/releases")
        maven("https://packages.aliyun.com/maven/repository/2428546-release-87ayOu") {
            credentials {
                username = "653b02ee970dc802e532f004"
                password = "ZwOcLGu7St6N"
            }
        }
        maven("https://jitpack.io")
    }
}

rootProject.name = "HMAutoUpdate"

include("fileserver")
