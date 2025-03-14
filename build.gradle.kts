group = "com.wpf.tools.hmautoupdate"
version = "1.0.0"

allprojects {
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://gitlab.com/api/v4/projects/38224197/packages/maven")
        maven("https://packages.aliyun.com/maven/repository/2428546-release-87ayOu") {
            credentials {
                username = "653b02ee970dc802e532f004"
                password = "ZwOcLGu7St6N"
            }
        }
        google()
        mavenCentral()
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.10" apply false
}
