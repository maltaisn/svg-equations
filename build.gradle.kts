buildscript {
    val kotlinVersion: String by project
    val proguardVersion: String by project
    val githubReleasePluginVersion: String by project
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath("com.guardsquare:proguard-gradle:$proguardVersion")
        classpath("com.github.breadmoirai:github-release:$githubReleasePluginVersion")
    }
}

plugins {
    base
}

allprojects {
    repositories {
        jcenter()
        mavenCentral()
    }
}

tasks.clean {
    delete(buildDir)
}
