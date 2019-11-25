buildscript {
    val kotlinVersion: String by project
    repositories {
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
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
