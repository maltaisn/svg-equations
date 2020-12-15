buildscript {
    val kotlinVersion: String by project
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath("com.guardsquare:proguard-gradle:7.0.1")
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
